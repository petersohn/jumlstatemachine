package com.kangirigungi.jstatemachine.componenttest;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.kangirigungi.jstatemachine.GuardNot;
import com.kangirigungi.jstatemachine.IEntryExitAction;
import com.kangirigungi.jstatemachine.IState;
import com.kangirigungi.jstatemachine.IStateMachineEngine;
import com.kangirigungi.jstatemachine.ITransitionAction;
import com.kangirigungi.jstatemachine.MockGuard;
import com.kangirigungi.jstatemachine.StateMachineEngine;


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

	private IStateMachineEngine<States, Events> stateMachine;
	private Actions lastAction;
	private States lastStateEntered;
	private States lastStateExited;

	private MockGuard<States, Events> isCdDetected;
	private MockGuard<States, Events> isLastTrack;

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
		Assert.assertEquals(nextState, stateMachine.getcurrentDeepState().getId());
		Assert.assertEquals(nextState, lastStateEntered);
		Assert.assertEquals(previousState, lastStateExited);
		Assert.assertEquals(action, lastAction);
	}

	@Before
	public void initialize() {
		lastStateEntered = null;
		lastStateExited = null;
		lastAction = null;

		EntryExitHandler entryExitHandler = new EntryExitHandler();
		isCdDetected = new MockGuard<States, Events>(false);
		isLastTrack = new MockGuard<States, Events>(false);

		stateMachine = new StateMachineEngine<States, Events>();

		stateMachine.addState(States.Empty).setEntryExitAction(entryExitHandler);
		stateMachine.addState(States.Stopped).setEntryExitAction(entryExitHandler);
		stateMachine.addState(States.Open).setEntryExitAction(entryExitHandler);
		IStateMachineEngine<States, Events> statePlaying =
				stateMachine.addCompositeState(States.Playing).
				setEntryExitAction(entryExitHandler).getStateMachine();

		statePlaying.addState(States.Playback).setEntryExitAction(entryExitHandler);
		statePlaying.addState(States.Paused).setEntryExitAction(entryExitHandler);

		stateMachine.setInitialState(States.Empty);
		statePlaying.setInitialState(States.Playback);

		stateMachine.addTransition(States.Empty,           null,
				new ActionHandler(Actions.StoreCdInfo),      States.Stopped,
				isCdDetected);
		stateMachine.addTransition(States.Empty,           Events.OpenClose,
				new ActionHandler(Actions.OpenDrawer),       States.Open);
		stateMachine.addTransition(States.Stopped,         Events.Play,
				new ActionHandler(Actions.StartPlayback),    States.Playing);
		stateMachine.addTransition(States.Stopped,         Events.OpenClose,
				new ActionHandler(Actions.OpenDrawer),       States.Open);
		stateMachine.addTransition(States.Playing,         Events.Stop,
				new ActionHandler(Actions.StopPlayback),     States.Stopped);
		stateMachine.addTransition(States.Playing,         Events.FastForward,
				new ActionHandler(Actions.StopPlayback),     States.Stopped,
				isLastTrack);
		stateMachine.addInternalTransition(States.Playing, Events.FastForward,
				new ActionHandler(Actions.ForwardTrack),
				new GuardNot<States, Events>(isLastTrack));
		stateMachine.addTransition(States.Playing,         Events.OpenClose,
				new ActionHandler(Actions.StopAndOpen),      States.Open);
		stateMachine.addTransition(States.Open,            Events.OpenClose,
				new ActionHandler(Actions.CloseDrawer),      States.Empty);

		statePlaying.addTransition(States.Playback,          Events.Pause,
				new ActionHandler(Actions.PausePlayback),   States.Paused);
		statePlaying.addTransition(States.Paused,          Events.Pause,
				new ActionHandler(Actions.ResumePlayback),   States.Playback);
	}

	@After
	public void finalizeTest() {
		System.out.println("");
	}

	@Test
	public void playOpenClosePlayPause2FastForward3() {
		System.out.println("playOpenClosePlayPause2FastForward3");
		isCdDetected.setValue(false);
		stateMachine.enter();
		checkState(null, States.Empty, null);

		stateMachine.processEvent(Events.Play);
		checkState(null, States.Empty, null);
		stateMachine.processEvent(Events.OpenClose);
		checkState(States.Empty, States.Open, Actions.OpenDrawer);
		isCdDetected.setValue(true);
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
		isLastTrack.setValue(true);
		stateMachine.processEvent(Events.FastForward);
		checkState(States.Playing, States.Stopped, Actions.StopPlayback);
	}

	@Test
	public void playPauseStopPlayPauseOpen() {
		System.out.println("playPauseStopPlayPauseOpen");
		isCdDetected.setValue(true);
		stateMachine.enter();
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
