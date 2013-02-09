/*
 * Copyright (c) <year>, <copyright holder>
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

package com.kangirigungi.jstatemachine;

import java.util.HashMap;
import java.util.Map;

public class StateMachine<StateId, Event> {

	private static class StateDescription<StateId, Event> {
		public IState<StateId, Event> state;
		public Map<Event, StateId> transitions;

		public StateDescription(IState<StateId, Event> state) {
			this.state = state;
			transitions = new HashMap<Event, StateId>();
		}
	}

	private Map<StateId, StateDescription<StateId, Event>> states;
	private StateId initialState;
	private StateId currentState;

	public StateMachine() {
		states = new HashMap<StateId, StateDescription<StateId, Event>>();
	}

	public IState<StateId, Event> addState(StateId id) {
		IState<StateId, Event> state = new State<StateId, Event>(this, id);
		states.put(id, new StateDescription<StateId, Event>(state));
		return state;
	}

	public void addTransition(StateId fromState, Event event, StateId toState)
			throws StateMachineException {
		StateDescription<StateId, Event> fromDescription = states.get(fromState);
		if (fromDescription == null) {
			throwNoStateException(fromState);
		}

		StateDescription<StateId, Event> toDescription = states.get(toState);
		if (toDescription == null) {
			throwNoStateException(toState);
		}


	}

	private void throwNoTransitionException(StateId fromState, Event event,
			StateId toState) throws NoTransitionException {
		throw new NoTransitionException("No transition from "+fromState.toString()+
				" to "+toState.toString()+" with event "+event.toString()+".",
				this, fromState, toState, event);
	}

	private void throwNoStateException(StateId state) throws NoStateException {
		throw new NoStateException("State "+state.toString()+
				" does not exist.", this, state);
	}


}
