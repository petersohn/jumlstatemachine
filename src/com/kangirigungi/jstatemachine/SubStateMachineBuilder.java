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

/**
 * A builder used to create a certain level of a state machine. Instances
 * of this class are acquired via {@link StateMachineBuilder#get()} (for top
 * level state machines) or {@link CompositeStateBuilder#getStateMachineBuilder()}
 * (for composite states).
 * <p>
 * The following kind of transitions are supported:
 * <ul>
 * <li><b>Normal transition:</b> Added with
 * {@link #addTransition(Object, Object, ITransitionAction, Object, IGuard) addTransition}
 * with the event parameter not <code>null</code>. These transitions are explicitly
 * triggered with {@link IStateMachine#processEvent(Object) processEvent} and change the
 * state (or exit then enter the same state if the source and target state
 * are the same).
 * <li><b>Internal transition:</b> Added with
 * {@link #addInternalTransition(Object, Object, ITransitionAction, IGuard) addInternalTransition}.
 * The event parameter cannot be <code>null</code>. These transitions trigger an action
 * without exiting the current state.
 * <li><b>Completion transition:</b> Added with
 * {@link #addTransition(Object, Object, ITransitionAction, Object, IGuard) addTransition}
 * with the event parameter <code>null</code>. These transitions are automatically
 * triggered after each successful transition. If guarded and the guard value
 * changes to true, it is cannot be checked automatically, only when the next
 * (internal or external) transition happens. It can also be triggered
 * explicitly with {@link IStateMachine#processEvent(Object) processEvent(null)}.
 * </ul>
 */
public class SubStateMachineBuilder<StateId, Event> {
	private IStateMachineEngine<StateId, Event> stateMachineEngine;

	SubStateMachineBuilder(
			IStateMachineEngine<StateId, Event> stateMachineEngine) {
		this.stateMachineEngine = stateMachineEngine;
	}

	/**
	 * Set the initial state of the state machine.
	 *
	 * @param initialState The initial state of the state machine.
	 * @return this.
	 */
	public SubStateMachineBuilder<StateId, Event> setInitialState(StateId initialState) {
		stateMachineEngine.setInitialState(initialState);
		return this;
	}

	/**
	 * Add a new state.
	 * <p>
	 * Only one state with one ID is allowed. If another
	 * one with the same ID is attempted to be added, an exception is thrown.
	 *
	 * @param id The identifier of the new state.
	 * @return The instance of the new state.
	 * @throws DuplicateStateException If the state id already exists.
	 */
	public StateBuilder<StateId, Event> addState(StateId id) {
		return new StateBuilder<StateId, Event>(stateMachineEngine.addState(id));
	}

	/**
	 * Add a new composite state.
	 * <p>
	 * Only one state with one id is allowed.
	 * If another one with the same ID is attempted to be added, an
	 * exception is thrown.
	 *
	 * @param id The identifier of the new state.
	 * @return The instance of the new state.
	 * @throws DuplicateStateException If the state id already exists.
	 */
	public CompositeStateBuilder<StateId, Event> addCompositeState(StateId id) {
		return new CompositeStateBuilder<StateId, Event>(
				stateMachineEngine.addCompositeState(id));
	}

	/**
	 * Add a new transition. The action and guard parameters are optional.
	 * The fromState parameter is mandatory and cannot be <code>null</code>. If event is
	 * <code>null</code>, that means it is a completion transition. Otherwise, it is a
	 * normal transition.
	 * <p>
	 * For each state and event, only one transition is allowed, except if
	 * all transitions for that state and event are guarded.
	 * <b>Warning:</b> There is no guarantee that all guard checks are
	 * executed. The first transition where the guard returns true is
	 * executed.
	 * <p>
	 * <b>Warning:</b> There is no check that completion transitions won't
	 * cause an infinite loop.
	 *
	 * @param fromState The initial state of the transition.
	 * @param event The event that triggers the transition. If <code>null</code>, it is a
	 * completion transition.
	 * @param action The action to be executed.
	 * @param toState The final state of the transition.
	 * @return this.
	 * @throws DuplicateTransitionException If there is an ambiguous transition.
	 * @throws {@link NoStateException} If either fromState of
	 * toState does not exist.
	 */
	public SubStateMachineBuilder<StateId, Event> addTransition(StateId fromState, Event event,
			ITransitionAction<StateId, Event> action, StateId toState,
			IGuard<StateId, Event> guard) {
		stateMachineEngine.addTransition(fromState, event, action, toState,
				guard);
		return this;
	}

	/**
	 * Same as {@link #addTransition(Object, Object, ITransitionAction, Object, IGuard)
	 * addTransition(fromState, event, action, toState, null)}.
	 */
	public SubStateMachineBuilder<StateId, Event> addTransition(StateId fromState, Event event,
			ITransitionAction<StateId, Event> action, StateId toState) {
		stateMachineEngine.addTransition(fromState, event, action, toState, null);
		return this;
	}


	/**
	 * Add a new internal transition. Internal transitions do not
	 * leave the state when performing an action. The action and
	 * guard parameters are optional (though it would not make sense
	 * omitting it). The state and event parameters is mandatory and
	 * cannot be <code>null</code>.
	 *
	 * @param state The initial state of the transition.
	 * @param event The event that triggers the transition.
	 * @param action The action to be executed.
	 * @return this.
	 * @throws DuplicateTransitionException If there is already a transition
	 * from the same state with the same event.
	 * @throws {@link NoStateException} If either fromState of
	 * toState does not exist.
	 */
	public SubStateMachineBuilder<StateId, Event> addInternalTransition(StateId state, Event event,
			ITransitionAction<StateId, Event> action,
			IGuard<StateId, Event> guard) {
		stateMachineEngine.addInternalTransition(state, event, action, guard);
		return this;
	}

	/**
	 * Same as {@link #addInternalTransition(Object, Object, ITransitionAction, IGuard)
	 * addInternalTransition(state, event, action, null)}.
	 */
	public SubStateMachineBuilder<StateId, Event> addInternalTransition(StateId state, Event event,
			ITransitionAction<StateId, Event> action) {
		stateMachineEngine.addInternalTransition(state, event, action, null);
		return this;
	}

}
