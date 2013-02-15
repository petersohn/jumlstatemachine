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

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.kangirigungi.jstatemachine.GuardNot;
import com.kangirigungi.jstatemachine.IEntryExitAction;
import com.kangirigungi.jstatemachine.IState;
import com.kangirigungi.jstatemachine.ITransitionAction;
import com.kangirigungi.jstatemachine.MockGuard;
import com.kangirigungi.jstatemachine.StateMachine;

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

	private StateMachine<States, Events> stateMachine;
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
		Assert.assertEquals(nextState, stateMachine.getcurrentState().getId());
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

		stateMachine = new StateMachine<States, Events>();

		stateMachine.addState(States.Empty).setEntryExitAction(entryExitHandler);
		stateMachine.addState(States.Stopped).setEntryExitAction(entryExitHandler);
		stateMachine.addState(States.Playing).setEntryExitAction(entryExitHandler);
		stateMachine.addState(States.Open).setEntryExitAction(entryExitHandler);
		stateMachine.addState(States.Paused).setEntryExitAction(entryExitHandler);

		stateMachine.setInitialState(States.Empty);

		stateMachine.addTransition(States.Empty,           null,
				new ActionHandler(Actions.StoreCdInfo),      States.Stopped,
				isCdDetected);
		stateMachine.addTransition(States.Empty,           Events.OpenClose,
				new ActionHandler(Actions.OpenDrawer),       States.Open);
		stateMachine.addTransition(States.Stopped,         Events.Play,
				new ActionHandler(Actions.StartPlayback),    States.Playing);
		stateMachine.addTransition(States.Stopped,         Events.OpenClose,
				new ActionHandler(Actions.OpenDrawer),       States.Open);
		stateMachine.addTransition(States.Playing,         Events.Pause,
				new ActionHandler(Actions.PausePlayback),    States.Paused);
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
		stateMachine.addTransition(States.Paused,          Events.Pause,
				new ActionHandler(Actions.ResumePlayback),   States.Playing);
		stateMachine.addTransition(States.Paused,          Events.Stop,
				new ActionHandler(Actions.StopPlayback),     States.Stopped);
		stateMachine.addTransition(States.Paused,          Events.OpenClose,
				new ActionHandler(Actions.StopAndOpen),      States.Open);
		stateMachine.addTransition(States.Open,            Events.OpenClose,
				new ActionHandler(Actions.CloseDrawer),      States.Empty);
	}

	@Override
	@After
	public void finalize() {
		System.out.println("");
	}

	@Test
	public void playFastForwardStopOpenClose() {
		System.out.println("playStopOpenClose");
		isCdDetected.setValue(true);
		stateMachine.start();
		checkState(States.Empty, States.Stopped, Actions.StoreCdInfo);

		stateMachine.processEvent(Events.Play);
		checkState(States.Stopped, States.Playing, Actions.StartPlayback);
		stateMachine.processEvent(Events.FastForward);
		checkState(States.Stopped, States.Playing, Actions.ForwardTrack);
		stateMachine.processEvent(Events.Stop);
		checkState(States.Playing, States.Stopped, Actions.StopPlayback);
		stateMachine.processEvent(Events.OpenClose);
		checkState(States.Stopped, States.Open, Actions.OpenDrawer);
		isCdDetected.setValue(false);
		stateMachine.processEvent(Events.OpenClose);
		checkState(States.Open, States.Empty, Actions.CloseDrawer);
	}

	@Test
	public void playOpenClosePlayPause2FastForward3() {
		System.out.println("playStopOpenClose");
		isCdDetected.setValue(false);
		stateMachine.start();
		checkState(null, States.Empty, null);

		stateMachine.processEvent(Events.Play);
		checkState(null, States.Empty, null);
		stateMachine.processEvent(Events.OpenClose);
		checkState(States.Empty, States.Open, Actions.OpenDrawer);
		isCdDetected.setValue(true);
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
		isLastTrack.setValue(true);
		stateMachine.processEvent(Events.FastForward);
		checkState(States.Playing, States.Stopped, Actions.StopPlayback);
	}


}
