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

/**
 * Representation of a state machine.
 * <p>
 * A state machine has two working phases. In each phase, different
 * methods can be called. If a method is called in the wrong state,
 * an exception is thrown.
 * <il>
 * <li><b>Not running:</b> The state machine starts in this phase.
 * The states and transitions of the state machine are defined here,
 * but the state machine has no current phase. When running, this
 * phase can be entered by the {@link stop()} method.
 * <li><b>Running:</b> The state machine has an actual state and
 * can process events. No states or transitions can be added while
 * the state machine is running. When not running, this
 * phase can be entered by the {@link start()} method.
 * </il>
 * <p>
 * Exceptions thown by this class are unchecked because the only way
 * they are thrown is because bad usage of the class and should never
 * happen in a well-written code.
 * 
 * @author Peter Szabados
 *
 * @param <Id> The type used for referencing states.
 * @param <Event> The type used for referencing events.
 */
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

	/**
	 * @return The initial state of the state machine.
	 */
	public IState<StateId, Event> getInitialState() {
		return initialState.state;
	}

	/**
	 * 
	 * @return The current state of the state machine.
	 * @throws NotRunningException When the state machine is not running.
	 */
	public IState<StateId, Event> getcurrentState() {
		checkRunning("getcurrentState");
		return currentState.state;
	}

	/**
	 * Get the state associated with the given id. If the state does not
	 * exist, an exception is thrown. States are added by the
	 * {@link addState(StateId) addState} method.
	 * @param id The identifier of the state.
	 * @return The state.
	 */
	public IState<StateId, Event> getState(StateId id) {
		return getStateDescription(id).state;
	}

	/**
	 * Set the initial state for the state machine. This must always
	 * be called before starting the state machine.
	 * @param initialState The new initial state.
	 * @throws AlreadyRunningException When the state machine is running.
	 */
	public void setInitialState(StateId initialState) {
		checkNotRunning("setInitialState");
		this.initialState = getStateDescription(initialState);
	}

	/**
	 * Start the state machine. When the state machine is started, no more
	 * states or transitions can be added.
	 * 
	 * @throws AlreadyRunningException When the state machine is running.
	 */
	public void start() {
		checkNotRunning("start");
		initialState.state.enterState(null);
		currentState = initialState;
	}

	/**
	 * Stop the state machine. States and transitions can only be added
	 * when the state machine is stopped.
	 */
	public void stop() {
		currentState = null;
	}
	
	/**
	 * @return True if the state machine is running.
	 */
	public boolean isRunning() {
		return currentState != null;
	}

	/**
	 * Add a new state. Only one state with one id is allowed. If another 
	 * one with the same idis attempted to be added, an exception is thrown.
	 * 
	 * @param id The identifier of the new state.
	 * @return The instance of the new state.
	 * @throw DuplicateStateException If the state id already exists.
	 */
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

	/**
	 * Add a new transition.
	 * 
	 * @param fromState The initial state of the transition.
	 * @param event The event that triggers the transition.
	 * @param action The action to be executed.
	 * @param toState The final state of the transition.
	 * @throw DuplicateTransitionException If there is already a transition
	 * from the same state with the same event.
	 * @throw {@link NoStateException} If either {@link fromState} of
	 * {@link toState} does not exist.
	 */
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

	/**
	 * Process an event and trigger any transitions needed to be done
	 * by the event.
	 * @param event The event to be processed.
	 */
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
