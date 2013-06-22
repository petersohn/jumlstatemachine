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

import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.kangirigungi.jstatemachine.FakeGuard;
import com.kangirigungi.jstatemachine.GuardNot;
import com.kangirigungi.jstatemachine.IEntryExitAction;
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

	private FakeGuard<States, Events> isCdDetected;
	private FakeGuard<States, Events> isLastTrack;

	private class ActionHandler implements
			ITransitionAction<States, Events> {

		private Actions action;

		public ActionHandler(Actions action) {
			this.action = action;
		}

		@Override
		public void onTransition(States fromState, States toState, Events event) {
			if (toState == null) {
				System.out.println(fromState+": "+
						event+"/"+action+" (internal)");
			} else {
				System.out.println(fromState+": "+
					event+"/"+action+" -> "+toState);
			}
			lastAction = action;
		}

	}

	private class EntryExitHandler implements IEntryExitAction<States, Events> {

		@Override
		public void onEnter(States state, Events event) {
			System.out.println("Entering "+state+
					" ("+event+")");
			lastStateEntered = state;
		}

		@Override
		public void onExit(States state, Events event) {
			System.out.println("Exiting "+state+
					" ("+event+")");
			lastStateExited = state;
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
	public void initialize() {
		lastStateEntered = null;
		lastStateExited = null;
		lastAction = null;

		EntryExitHandler entryExitHandler = new EntryExitHandler();
		isCdDetected = new FakeGuard<States, Events>(false);
		isLastTrack = new FakeGuard<States, Events>(false);

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
	public void playOpenClosePlayPause2FastForward3() {
		System.out.println("playOpenClosePlayPause2FastForward3");
		isCdDetected.setValue(false);
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
