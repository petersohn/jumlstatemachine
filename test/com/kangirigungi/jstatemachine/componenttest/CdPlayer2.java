/*
 * Copyright (c) 2013, Peter Szabados
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 *     (1) Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *     (2) Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in
 *     the documentation and/or other materials provided with the
 *     distribution.
 *
 *     (3)The name of the author may not be used to
 *     endorse or promote products derived from this software without
 *     specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING
 * IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package com.kangirigungi.jstatemachine.componenttest;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import junit.framework.Assert;

import org.junit.After;
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

public class CdPlayer2 {

	private static enum States {
		Empty, Stopped, Playing, Paused, Open
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
		Assert.assertEquals(nextState, stateMachine.getCurrentState());
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
		mainStateMachine.addState(States.Playing).setEntryExitAction(entryExitHandler);
		mainStateMachine.addState(States.Open).setEntryExitAction(entryExitHandler);
		mainStateMachine.addState(States.Paused).setEntryExitAction(entryExitHandler);

		mainStateMachine.setInitialState(States.Empty);

		mainStateMachine.addTransition(States.Empty,           null,
				new ActionHandler(Actions.StoreCdInfo),      States.Stopped,
				isCdDetected);
		mainStateMachine.addTransition(States.Empty,           Events.OpenClose,
				new ActionHandler(Actions.OpenDrawer),       States.Open);
		mainStateMachine.addTransition(States.Stopped,         Events.Play,
				new ActionHandler(Actions.StartPlayback),    States.Playing);
		mainStateMachine.addTransition(States.Stopped,         Events.OpenClose,
				new ActionHandler(Actions.OpenDrawer),       States.Open);
		mainStateMachine.addTransition(States.Playing,         Events.Pause,
				new ActionHandler(Actions.PausePlayback),    States.Paused);
		mainStateMachine.addTransition(States.Playing,         Events.Stop,
				new ActionHandler(Actions.StopPlayback),     States.Stopped);
		mainStateMachine.addTransition(States.Playing,         Events.FastForward,
				new ActionHandler(Actions.StopPlayback),     States.Stopped,
				isLastTrack);
		mainStateMachine.addInternalTransition(States.Playing, Events.FastForward,
				new ActionHandler(Actions.ForwardTrack),
				new GuardNot<States, Events>(isLastTrack));
		mainStateMachine.addTransition(States.Playing,         Events.OpenClose,
				new ActionHandler(Actions.StopAndOpen),      States.Open);
		mainStateMachine.addTransition(States.Paused,          Events.Pause,
				new ActionHandler(Actions.ResumePlayback),   States.Playing);
		mainStateMachine.addTransition(States.Paused,          Events.Stop,
				new ActionHandler(Actions.StopPlayback),     States.Stopped);
		mainStateMachine.addTransition(States.Paused,          Events.OpenClose,
				new ActionHandler(Actions.StopAndOpen),      States.Open);
		mainStateMachine.addTransition(States.Open,            Events.OpenClose,
				new ActionHandler(Actions.CloseDrawer),      States.Empty);

		stateMachine = stateMachineBuilder.create();
	}

	@After
	public void finalizeTest() {
		System.out.println("");
	}

	@Test
	@SuppressWarnings("unchecked")
	public void playFastForwardStopOpenClose() {
		System.out.println("playFastForwardStopOpenClose");
		when(isCdDetected.checkTransition(
				any(IState.class), any(IState.class), any(Events.class))).
				thenReturn(true);
		stateMachine.processEvent(null);
		checkState(States.Empty, States.Stopped, Actions.StoreCdInfo);

		stateMachine.processEvent(Events.Play);
		checkState(States.Stopped, States.Playing, Actions.StartPlayback);
		stateMachine.processEvent(Events.FastForward);
		checkState(States.Stopped, States.Playing, Actions.ForwardTrack);
		stateMachine.processEvent(Events.Stop);
		checkState(States.Playing, States.Stopped, Actions.StopPlayback);
		stateMachine.processEvent(Events.OpenClose);
		checkState(States.Stopped, States.Open, Actions.OpenDrawer);
		when(isCdDetected.checkTransition(
				any(IState.class), any(IState.class), any(Events.class))).
				thenReturn(false);
		stateMachine.processEvent(Events.OpenClose);
		checkState(States.Open, States.Empty, Actions.CloseDrawer);
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
		checkState(States.Stopped, States.Playing, Actions.StartPlayback);
		stateMachine.processEvent(Events.Pause);
		checkState(States.Playing, States.Paused, Actions.PausePlayback);
		stateMachine.processEvent(Events.Pause);
		checkState(States.Paused, States.Playing, Actions.ResumePlayback);
		stateMachine.processEvent(Events.FastForward);
		checkState(States.Paused, States.Playing, Actions.ForwardTrack);
		stateMachine.processEvent(Events.FastForward);
		checkState(States.Paused, States.Playing, Actions.ForwardTrack);
		when(isLastTrack.checkTransition(
				any(IState.class), any(IState.class), any(Events.class))).
				thenReturn(true);
		stateMachine.processEvent(Events.FastForward);
		checkState(States.Playing, States.Stopped, Actions.StopPlayback);
	}


}
