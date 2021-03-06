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

import com.kangirigungi.jstatemachine.IEntryExitAction;
import com.kangirigungi.jstatemachine.IStateMachine;
import com.kangirigungi.jstatemachine.ITransitionAction;
import com.kangirigungi.jstatemachine.StateMachineBuilder;
import com.kangirigungi.jstatemachine.SubStateMachineBuilder;

public class CdPlayer {

	private static enum States {
		Empty, Stopped, Playing, Paused, Open
	}

	private static enum Events {
		CdDetected, Play, Stop, Pause, OpenClose
	}

	private static enum Actions {
		 StoreCdInfo, StartPlayback, StopPlayback, PausePlayback,
		 ResumePlayback, StopAndOpen, OpenDrawer, CloseDrawer
	}

	private IStateMachine<States, Events> stateMachine;
	private Actions lastAction;
	private States lastStateEntered;
	private States lastStateExited;

	private class ActionHandler implements
			ITransitionAction<States, Events> {

		private Actions action;

		public ActionHandler(Actions action) {
			this.action = action;
		}

		@Override
		public void onTransition(States fromState, States toState, Events event) {
			System.out.println(fromState+": "+
				event+"/"+action+" -> "+toState);
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
		Assert.assertEquals(nextState, stateMachine.getCurrentState());
		Assert.assertEquals(nextState, lastStateEntered);
		Assert.assertEquals(previousState, lastStateExited);
		Assert.assertEquals(action, lastAction);
	}

	@Before
	public void initialize() {
		EntryExitHandler entryExitHandler = new EntryExitHandler();
		StateMachineBuilder<States, Events> stateMachineBuilder =
				new StateMachineBuilder<States, Events>();
		SubStateMachineBuilder<States, Events> mainStateMachine =
				stateMachineBuilder.get();

		mainStateMachine.addState(States.Empty).setEntryExitAction(entryExitHandler);
		mainStateMachine.addState(States.Stopped).setEntryExitAction(entryExitHandler);
		mainStateMachine.addState(States.Playing).setEntryExitAction(entryExitHandler);
		mainStateMachine.addState(States.Open).setEntryExitAction(entryExitHandler);
		mainStateMachine.addState(States.Paused).setEntryExitAction(entryExitHandler);

		mainStateMachine.setInitialState(States.Empty)

			.addTransition(States.Empty,    Events.CdDetected,
				new ActionHandler(Actions.StoreCdInfo),    States.Stopped)
			.addTransition(States.Empty,    Events.OpenClose,
				new ActionHandler(Actions.OpenDrawer),     States.Open)
			.addTransition(States.Stopped,  Events.Play,
				new ActionHandler(Actions.StartPlayback),  States.Playing)
			.addTransition(States.Stopped,  Events.OpenClose,
				new ActionHandler(Actions.OpenDrawer),     States.Open)
			.addTransition(States.Playing,  Events.Pause,
				new ActionHandler(Actions.PausePlayback),  States.Paused)
			.addTransition(States.Playing,  Events.Stop,
				new ActionHandler(Actions.StopPlayback),   States.Stopped)
			.addTransition(States.Playing,  Events.OpenClose,
				new ActionHandler(Actions.StopAndOpen),    States.Open)
			.addTransition(States.Paused,   Events.Pause,
				new ActionHandler(Actions.ResumePlayback), States.Playing)
			.addTransition(States.Paused,   Events.Stop,
				new ActionHandler(Actions.StopPlayback),   States.Stopped)
			.addTransition(States.Paused,   Events.OpenClose,
				new ActionHandler(Actions.StopAndOpen),    States.Open)
			.addTransition(States.Open,     Events.OpenClose,
				new ActionHandler(Actions.CloseDrawer),    States.Empty);

		stateMachine = stateMachineBuilder.create();
	}

	@After
	public void finalizeTest() {
		System.out.println("");
	}

	@Test
	public void playStopOpenClose() {
		System.out.println("playStopOpenClose");
		Assert.assertEquals(States.Empty, stateMachine.getCurrentState());

		stateMachine.processEvent(Events.CdDetected);
		checkState(States.Empty, States.Stopped, Actions.StoreCdInfo);
		stateMachine.processEvent(Events.Play);
		checkState(States.Stopped, States.Playing, Actions.StartPlayback);
		stateMachine.processEvent(Events.Stop);
		checkState(States.Playing, States.Stopped, Actions.StopPlayback);
		stateMachine.processEvent(Events.OpenClose);
		checkState(States.Stopped, States.Open, Actions.OpenDrawer);
		stateMachine.processEvent(Events.OpenClose);
		checkState(States.Open, States.Empty, Actions.CloseDrawer);
	}

	@Test
	public void openClosePlayPause3StopPlayOpen() {
		System.out.println("openClosePlayPause3StopPlayOpen");
		Assert.assertEquals(States.Empty, stateMachine.getCurrentState());

		stateMachine.processEvent(Events.OpenClose);
		checkState(States.Empty, States.Open, Actions.OpenDrawer);
		stateMachine.processEvent(Events.OpenClose);
		checkState(States.Open, States.Empty, Actions.CloseDrawer);
		stateMachine.processEvent(Events.CdDetected);
		checkState(States.Empty, States.Stopped, Actions.StoreCdInfo);
		stateMachine.processEvent(Events.Play);
		checkState(States.Stopped, States.Playing, Actions.StartPlayback);
		stateMachine.processEvent(Events.Pause);
		checkState(States.Playing, States.Paused, Actions.PausePlayback);
		stateMachine.processEvent(Events.Pause);
		checkState(States.Paused, States.Playing, Actions.ResumePlayback);
		stateMachine.processEvent(Events.Pause);
		checkState(States.Playing, States.Paused, Actions.PausePlayback);
		stateMachine.processEvent(Events.Stop);
		checkState(States.Paused, States.Stopped, Actions.StopPlayback);
		stateMachine.processEvent(Events.Play);
		checkState(States.Stopped, States.Playing, Actions.StartPlayback);
		stateMachine.processEvent(Events.OpenClose);
		checkState(States.Playing, States.Open, Actions.StopAndOpen);
	}
}
