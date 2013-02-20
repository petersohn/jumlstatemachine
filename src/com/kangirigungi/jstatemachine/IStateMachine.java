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

public interface IStateMachine<StateId, Event> {

	/**
	 * @return The initial state of the state machine.
	 */
	public IState<StateId, Event> getInitialState();

	/**
	 *
	 * @return The current state of the state machine.
	 * @throws NotRunningException When the state machine is not running.
	 */
	public IState<StateId, Event> getcurrentState();

	/**
	 * Get the state associated with the given id. If the state does not
	 * exist, an exception is thrown. States are added by the
	 * {@link #addState(Object) addState} method.
	 * @param id The identifier of the state.
	 * @return The state.
	 */
	public IState<StateId, Event> getState(StateId id);

	public boolean hasState(StateId id);

	public IStateMachine<StateId, Event> getTopLevelStateMachine();

	/**
	 * Set the initial state for the state machine. This must always
	 * be called before starting the state machine.
	 * @param initialState The new initial state.
	 * @throws AlreadyRunningException When the state machine is running.
	 */
	public void setInitialState(StateId initialState);

	/**
	 * Start the state machine. When the state machine is started, no more
	 * states or transitions can be added. The entry action of the initial
	 * state is executed.
	 * <p>
	 * If an exception is thrown from the entry action, then the state
	 * machine is not started.
	 *
	 * @throws AlreadyRunningException When the state machine is running.
	 */
	public void start();

	/**
	 * Stop the state machine. No more events can be triggered after the
	 * state machine is stopped. The exit action of the current state is
	 * executed.
	 * <p>
	 * If an exception is thrown from the exit action, then the state
	 * machine is not stopped.
	 */
	public void stop();

	/**
	 * @return True if the state machine is running.
	 */
	public boolean isRunning();

	/**
	 * Add a new state. Only one state with one id is allowed. If another
	 * one with the same idis attempted to be added, an exception is thrown.
	 *
	 * @param id The identifier of the new state.
	 * @return The instance of the new state.
	 * @throws DuplicateStateException If the state id already exists.
	 */
	public IState<StateId, Event> addState(StateId id);
	public ICompositeState<StateId, Event> addCompositeState(StateId id);

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
	 * @throws DuplicateTransitionException If there is an ambiguous transition.
	 * @throws {@link NoStateException} If either fromState of
	 * toState does not exist.
	 */
	public void addTransition(StateId fromState, Event event,
			ITransitionAction<StateId, Event> action, StateId toState,
			IGuard<StateId, Event> guard);

	/**
	 * Same as {@link #addTransition(Object, Object, ITransitionAction, Object, IGuard)
	 * addTransition(fromState, event, action, toState, null)}.
	 */
	public void addTransition(StateId fromState, Event event,
			ITransitionAction<StateId, Event> action, StateId toState);

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
	 * @throws DuplicateTransitionException If there is already a transition
	 * from the same state with the same event.
	 * @throws {@link NoStateException} If either fromState of
	 * toState does not exist.
	 */
	public void addInternalTransition(StateId state, Event event,
			ITransitionAction<StateId, Event> action,
			IGuard<StateId, Event> guard);

	/**
	 * Same as {@link #addInternalTransition(Object, Object, ITransitionAction, IGuard)
	 * addInternalTransition(state, event, action, null)}.
	 */
	public void addInternalTransition(StateId state, Event event,
			ITransitionAction<StateId, Event> action);

	/**
	 * Process an event and trigger any transitions needed to be done
	 * by the event. It must not be called from within callbacks. If
	 * called while it is already running, an exception is thrown.
	 * <p>
	 * The <code>null</code> value of event is special: it represents completion
	 * transitions. It is automatically processed after each transition.
	 * It can also be explicitly triggered (for example if there is a
	 * guarded completion transition and the guard value may have changed).
	 * <p>
	 * If an exception is thrown from a callback, the state machine doesn't
	 * change state. If the exit action of a state is already called (the
	 * exception is thrown from the action or the entry action of the next
	 * state) then the entry action of the original state is called again.
	 * After this, the exception is rethrown. No completion transitions
	 * are triggered after such exceptions.
	 *
	 * @param event The event to be processed.
	 * @throws StateMachineException for various cases of improper usage.
	 */
	public void processEvent(Event event);

}