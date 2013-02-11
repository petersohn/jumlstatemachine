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

package com.kangirigungi.jstatemachine;

import java.util.HashMap;
import java.util.Map;

public class StateMachine<StateId, Event> {

	private static class TransitionTarget<StateId, Event> {
		public StateDescription<StateId, Event> targetState;
		public ITransitionAction<StateId, Event> action;
		
		public TransitionTarget(StateDescription<StateId, Event> targetState,
				ITransitionAction<StateId, Event> action) {
			this.targetState = targetState;
			this.action = action;
		}
	}
	
	private static class StateDescription<StateId, Event> {
		public IState<StateId, Event> state;
		public Map<Event, TransitionTarget<StateId, Event>> transitions;

		public StateDescription(IState<StateId, Event> state) {
			this.state = state;
			transitions = new HashMap<Event, TransitionTarget<StateId, Event>>();
		}
	}

	private IStateFactory<StateId, Event> stateFactory;
	private Map<StateId, StateDescription<StateId, Event>> states;
	private StateDescription<StateId, Event> initialState;
	private StateDescription<StateId, Event> currentState;

	public StateMachine() {
		stateFactory = new StateFactory<StateId, Event>();
		states = new HashMap<StateId, StateDescription<StateId, Event>>();
	}

	public IState<StateId, Event> getInitialState() {
		return initialState.state;
	}

	public IState<StateId, Event> getcurrentState() {
		checkRunning("getcurrentState");
		return currentState.state;
	}

	public IState<StateId, Event> getState(StateId id) {
		return getStateDescription(id).state;
	}

	public void setInitialState(StateId initialState) {
		checkNotRunning("setInitialState");
		this.initialState = getStateDescription(initialState);
	}

	public void start() {
		checkNotRunning("start");
		initialState.state.enterState(null);
		currentState = initialState;
	}

	public void stop() {
		currentState = null;
	}
	
	public boolean isRunning() {
		return currentState != null;
	}

	public IState<StateId, Event> addState(StateId id) {
		if (states.get(id) != null) {
			throw new DuplicateStateException(
					"Duplicate state: "+id.toString()+".",
					this, id);
		}

		IState<StateId, Event> state =
				stateFactory.createStete(this, id);
		states.put(id, new StateDescription<StateId, Event>(state));
		return state;
	}

	public void addTransition(StateId fromState, Event event, 
			ITransitionAction<StateId, Event> action,
			StateId toState) {
		checkNotRunning("addTransition");

		StateDescription<StateId, Event> fromDescription =
				getStateDescription(fromState);
		StateDescription<StateId, Event> toDescription =
				getStateDescription(toState);

		if (fromDescription.transitions.get(event) != null) {
			throw new DuplicateTransitionException(
					"Duplicate transition from state "+fromState.toString()+
					" by event "+event.toString()+".",
					this, fromState, event);
		}

		fromDescription.transitions.put(event, 
				new TransitionTarget<StateId, Event>(toDescription, action));
	}

	public void processEvent(Event event) {
		checkRunning("processEvent");
		TransitionTarget<StateId, Event> target =
				currentState.transitions.get(event);
		if (target != null) {
			// change the state
			currentState.state.exitState(event);
			if (target.action != null) {
				target.action.onTransition(currentState.state, 
						target.targetState.state, event);
			}
			target.targetState.state.enterState(event);
			currentState = target.targetState;
		} else {
			// delegate the event
			currentState.state.processEvent(event);
		}
	}

	private void checkRunning(String action) {
		if (!isRunning()) {
			throw new NotRunningException(
					"Cannot execute \""+action+
					"\" while the state machine is not running.");
		}
	}

	private void checkNotRunning(String action) {
		if (isRunning()) {
			throw new AlreadyRunningException(
					"Cannot execute \""+action+
					"\" while the state machine is running.");
		}
	}

	private StateDescription<StateId, Event> getStateDescription(StateId id)
			throws NoStateException {
		StateDescription<StateId, Event> result = states.get(id);
		if (result == null) {
			throwNoStateException(id);
		}
		return result;
	}

	private void throwNoStateException(StateId state) {
		throw new NoStateException("State "+state.toString()+
				" does not exist.", this, state);
	}

	void setStateFactory(IStateFactory<StateId, Event> stateFactory) {
		this.stateFactory = stateFactory;
	}

}
