package com.kangirigungi.jstatemachine.componenttest;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.kangirigungi.jstatemachine.GuardNot;
import com.kangirigungi.jstatemachine.IEntryExitAction;
import com.kangirigungi.jstatemachine.IGuard;
import com.kangirigungi.jstatemachine.IState;
import com.kangirigungi.jstatemachine.IStateMachine;
import com.kangirigungi.jstatemachine.ITransitionAction;
import com.kangirigungi.jstatemachine.StateMachineBuilder;
import com.kangirigungi.jstatemachine.SubStateMachineBuilder;


public class CdPlayerComposite {
	private static enum States {
		Empty, Stopped, Playing, Playback, Paused, Open
	}

	private static enum Events {
		Play, Stop, Pause, OpenClose, FastForward
	}

	private static enum Actions {
		 StoreCdInfo, StartPlayback, StopPlayback, PausePlayback,
		 ResumePlayback, StopAndOpen, OpenDrawer, CloseDrawer,
		 ForwardTrack
	}

	private IStateMachine<States, Events> stateMachine;
	private Actions lastAction;
	private States lastStateEntered;
	private States lastStateExited;

	private IGuard<States, Events> isCdDetected;
	private IGuard<States, Events> isLastTrack;

	private class ActionHandler implements
			ITransitionAction<States, Events> {

		private Actions action;

		public ActionHandler(Actions action) {
			this.action = action;
		}

		@Override
		public void onTransition(IState<States, Events> fromState,
				IState<States, Events> toState, Events event) {
			if (toState == null) {
				System.out.println(fromState.getId()+": "+
						event+"/"+action+" (internal)");
			} else {
				System.out.println(fromState.getId()+": "+
					event+"/"+action+" -> "+toState.getId());
			}
			lastAction = action;
		}

	}

	private class EntryExitHandler implements IEntryExitAction<States, Events> {

		@Override
		public void onEnter(IState<States, Events> state, Events event) {
			System.out.println("Entering "+state.getId()+
					" ("+event+")");
			lastStateEntered = state.getId();
		}

		@Override
		public void onExit(IState<States, Events> state, Events event) {
			System.out.println("Exiting "+state.getId()+
					" ("+event+")");
			lastStateExited = state.getId();
		}
	}

	private void checkState(States previousState, States nextState, Actions action) {
		List<States> currentStates = stateMachine.getCurrentStates();
		Assert.assertEquals(nextState, currentStates.get(currentStates.size()-1));
		Assert.assertEquals(nextState, lastStateEntered);
		Assert.assertEquals(previousState, lastStateExited);
		Assert.assertEquals(action, lastAction);
	}

	@Before
	@SuppressWarnings("unchecked")
	public void initialize() {
		lastStateEntered = null;
		lastStateExited = null;
		lastAction = null;

		EntryExitHandler entryExitHandler = new EntryExitHandler();
		isCdDetected = Mockito.mock(IGuard.class);
		when(isCdDetected.checkTransition(
				any(IState.class), any(IState.class), any(Events.class))).
				thenReturn(false);
		isLastTrack = Mockito.mock(IGuard.class);
		when(isLastTrack.checkTransition(
				any(IState.class), any(IState.class), any(Events.class))).
				thenReturn(false);

		StateMachineBuilder<States, Events> stateMachineBuilder =
				new StateMachineBuilder<States, Events>();
		SubStateMachineBuilder<States, Events> mainStateMachine =
				stateMachineBuilder.get();


		mainStateMachine.addState(States.Empty).setEntryExitAction(entryExitHandler);
		mainStateMachine.addState(States.Stopped).setEntryExitAction(entryExitHandler);
		mainStateMachine.addState(States.Open).setEntryExitAction(entryExitHandler);
		SubStateMachineBuilder<States, Events> statePlaying =
				mainStateMachine.addCompositeState(States.Playing).
				setEntryExitAction(entryExitHandler).
				getStateMachineBuilder();

		statePlaying.addState(States.Playback).setEntryExitAction(entryExitHandler);
		statePlaying.addState(States.Paused).setEntryExitAction(entryExitHandler);

		mainStateMachine.setInitialState(States.Empty)

			.addTransition(States.Empty,           null,
				new ActionHandler(Actions.StoreCdInfo),      States.Stopped,
				isCdDetected)
			.addTransition(States.Empty,           Events.OpenClose,
				new ActionHandler(Actions.OpenDrawer),       States.Open)
			.addTransition(States.Stopped,         Events.Play,
				new ActionHandler(Actions.StartPlayback),    States.Playing)
			.addTransition(States.Stopped,         Events.OpenClose,
				new ActionHandler(Actions.OpenDrawer),       States.Open)
			.addTransition(States.Playing,         Events.Stop,
				new ActionHandler(Actions.StopPlayback),     States.Stopped)
			.addTransition(States.Playing,         Events.FastForward,
				new ActionHandler(Actions.StopPlayback),     States.Stopped,
				isLastTrack)
			.addInternalTransition(States.Playing, Events.FastForward,
				new ActionHandler(Actions.ForwardTrack),
				new GuardNot<States, Events>(isLastTrack))
			.addTransition(States.Playing,         Events.OpenClose,
				new ActionHandler(Actions.StopAndOpen),      States.Open)
			.addTransition(States.Open,            Events.OpenClose,
				new ActionHandler(Actions.CloseDrawer),      States.Empty);

		statePlaying.setInitialState(States.Playback)

			.addTransition(States.Playback,          Events.Pause,
				new ActionHandler(Actions.PausePlayback),   States.Paused)
			.addTransition(States.Paused,          Events.Pause,
				new ActionHandler(Actions.ResumePlayback),   States.Playback);

		stateMachine = stateMachineBuilder.create();
	}

	@After
	public void finalizeTest() {
		System.out.println("");
	}

	@Test
	@SuppressWarnings("unchecked")
	public void playOpenClosePlayPause2FastForward3() {
		System.out.println("playOpenClosePlayPause2FastForward3");
		when(isCdDetected.checkTransition(
				any(IState.class), any(IState.class), any(Events.class))).
				thenReturn(false);
		checkState(null, States.Empty, null);

		stateMachine.processEvent(Events.Play);
		checkState(null, States.Empty, null);
		stateMachine.processEvent(Events.OpenClose);
		checkState(States.Empty, States.Open, Actions.OpenDrawer);
		when(isCdDetected.checkTransition(
				any(IState.class), any(IState.class), any(Events.class))).
				thenReturn(true);
		stateMachine.processEvent(Events.OpenClose);
		checkState(States.Empty, States.Stopped, Actions.StoreCdInfo);
		stateMachine.processEvent(Events.Play);
		checkState(States.Stopped, States.Playback, Actions.StartPlayback);
		stateMachine.processEvent(Events.Pause);
		checkState(States.Playback, States.Paused, Actions.PausePlayback);
		stateMachine.processEvent(Events.Pause);
		checkState(States.Paused, States.Playback, Actions.ResumePlayback);
		stateMachine.processEvent(Events.FastForward);
		checkState(States.Paused, States.Playback, Actions.ForwardTrack);
		stateMachine.processEvent(Events.FastForward);
		checkState(States.Paused, States.Playback, Actions.ForwardTrack);
		when(isLastTrack.checkTransition(
				any(IState.class), any(IState.class), any(Events.class))).
				thenReturn(true);
		stateMachine.processEvent(Events.FastForward);
		checkState(States.Playing, States.Stopped, Actions.StopPlayback);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void playPauseStopPlayPauseOpen() {
		System.out.println("playPauseStopPlayPauseOpen");
		when(isCdDetected.checkTransition(
				any(IState.class), any(IState.class), any(Events.class))).
				thenReturn(true);
		stateMachine.processEvent(null);
		checkState(States.Empty, States.Stopped, Actions.StoreCdInfo);
		stateMachine.processEvent(Events.Play);
		checkState(States.Stopped, States.Playback, Actions.StartPlayback);
		stateMachine.processEvent(Events.Pause);
		checkState(States.Playback, States.Paused, Actions.PausePlayback);
		stateMachine.processEvent(Events.Stop);
		checkState(States.Playing, States.Stopped, Actions.StopPlayback);
		stateMachine.processEvent(Events.Play);
		checkState(States.Stopped, States.Playback, Actions.StartPlayback);
		stateMachine.processEvent(Events.Pause);
		checkState(States.Playback, States.Paused, Actions.PausePlayback);
		stateMachine.processEvent(Events.OpenClose);
		checkState(States.Playing, States.Open, Actions.StopAndOpen);
	}
}
