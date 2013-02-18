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

import com.kangirigungi.jstatemachine.IEntryExitAction;
import com.kangirigungi.jstatemachine.IState;
import com.kangirigungi.jstatemachine.IStateMachine;
import com.kangirigungi.jstatemachine.ITransitionAction;
import com.kangirigungi.jstatemachine.StateMachine;

/**
 * Class for the example on the wiki page.
 * @author P�ter Szabados
 *
 */
public class Main {

	private static enum States {
		Empty, Stopped, Playing, Paused, Open
	}

	private static enum Events {
		CdDetected, Play, Stop, Pause, OpenClose
	}

	private static class ActionHandler implements
			ITransitionAction<States, Events> {

		private String action;

		public ActionHandler(String action) {
			this.action = action;
		}

		@Override
		public void onTransition(IState<States, Events> fromState,
				IState<States, Events> toState, Events event) {
			System.out.println(fromState.getId()+": "+
				event+"/"+action+" -> "+toState.getId());
		}

	}

	private static class EntryExitHandler implements IEntryExitAction<States, Events> {

		@Override
		public void onEnter(IState<States, Events> state, Events event) {
			System.out.println("Entering "+state.getId()+
					" ("+event+")");
		}

		@Override
		public void onExit(IState<States, Events> state, Events event) {
			System.out.println("Exiting "+state.getId()+
					" ("+event+")");
		}

	}

	public static void main(String[] args) {
		EntryExitHandler entryExitHandler = new EntryExitHandler();
		IStateMachine<States, Events> stateMachine =
				new StateMachine<States, Events>();

		// define states
		stateMachine.addState(States.Empty).setEntryExitAction(entryExitHandler);
		stateMachine.addState(States.Stopped).setEntryExitAction(entryExitHandler);
		stateMachine.addState(States.Playing).setEntryExitAction(entryExitHandler);
		stateMachine.addState(States.Open).setEntryExitAction(entryExitHandler);
		stateMachine.addState(States.Paused).setEntryExitAction(entryExitHandler);

		stateMachine.setInitialState(States.Empty);

		// define transactions
		stateMachine.addTransition(States.Empty,    Events.CdDetected,
				new ActionHandler("StoreCdInfo"),    States.Stopped);
		stateMachine.addTransition(States.Empty,    Events.OpenClose,
				new ActionHandler("OpenDrawer"),     States.Open);
		stateMachine.addTransition(States.Stopped,  Events.Play,
				new ActionHandler("StartPlayback"),  States.Playing);
		stateMachine.addTransition(States.Stopped,  Events.OpenClose,
				new ActionHandler("OpenDrawer"),     States.Open);
		stateMachine.addTransition(States.Playing,  Events.Pause,
				new ActionHandler("PausePlayback"),  States.Paused);
		stateMachine.addTransition(States.Playing,  Events.Stop,
				new ActionHandler("StopPlayback"),   States.Stopped);
		stateMachine.addTransition(States.Playing,  Events.OpenClose,
				new ActionHandler("StopAndOpen"),    States.Open);
		stateMachine.addTransition(States.Paused,   Events.Pause,
				new ActionHandler("ResumePlayback"), States.Playing);
		stateMachine.addTransition(States.Paused,   Events.Stop,
				new ActionHandler("StopPlayback"),   States.Stopped);
		stateMachine.addTransition(States.Paused,   Events.OpenClose,
				new ActionHandler("StopAndOpen"),    States.Open);
		stateMachine.addTransition(States.Open,     Events.OpenClose,
				new ActionHandler("CloseDrawer"),    States.Empty);
		stateMachine.start();

		// process events
		stateMachine.processEvent(Events.OpenClose);
		stateMachine.processEvent(Events.OpenClose);
		stateMachine.processEvent(Events.CdDetected);
		stateMachine.processEvent(Events.Play);
		stateMachine.processEvent(Events.Pause);
		stateMachine.processEvent(Events.Pause);
		stateMachine.processEvent(Events.Pause);
		stateMachine.processEvent(Events.Stop);
		stateMachine.processEvent(Events.Play);
		stateMachine.processEvent(Events.OpenClose);
	}

}
