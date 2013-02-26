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
 * Representation of a state machine.
 * <p>
 * A state machine has two working phases. In each phase, different
 * methods can be called. If a method is called in the wrong state,
 * an exception is thrown.
 * <ul>
 * <li><b>Not running:</b> The state machine starts in this phase.
 * The states and transitions of the state machine are defined here,
 * but the state machine has no current phase. When running, this
 * phase can be entered by the {@link #stop()} method.
 * <li><b>Running:</b> The state machine has an actual state and
 * can process events. No states or transitions can be added while
 * the state machine is running. When not running, this
 * phase can be entered by the {@link #start()} method.
 * </ul>
 * <p>
 * The following kind of transitions are supported:
 * <ul>
 * <li><b>Normal transition:</b> Added with
 * {@link #addTransition(Object, Object, ITransitionAction, Object, IGuard) addTransition}
 * with the event parameter not <code>null</code>. These transitions are explicitly
 * triggered with {@link #processEvent(Object) processEvent} and change the
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
 * explicitly with {@link #processEvent(Object) processEvent(null)}.
 * </ul>
 * <p>
 * This class (and the entire library) is not thread-safe. This means
 * that in order to use it from within multiple threads, calls to any
 * methods (typically {@link #processEvent(Object) processEvent})
 * must be synchronized.
 * <p>
 * Exceptions thrown by this class are unchecked because the only way
 * they are thrown is because bad usage of the class and should never
 * happen in a well-written code.
 *
 * @author Peter Szabados
 *
 * @param <StateId> The type used for referencing states.
 * @param <Event> The type used for referencing events.
 */
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
	 *
	 * @return The current state of the state machine. If the current state
	 * is a composite state, return the deepest state we are in.
	 * @throws NotRunningException When the state machine is not running.
	 */
	public IState<StateId, Event> getcurrentDeepState();


	/**
	 * Get the state associated with the given id. If the state does not
	 * exist, an exception is thrown. States are added by the
	 * {@link #addState(Object) addState} method.
	 * @param id The identifier of the state.
	 * @return The state.
	 */
	public IState<StateId, Event> getState(StateId id);

	/**
	 * Find out whether the state machine or any of its substates recursivels
	 * have a certain state.
	 * @param id The id of the state.
	 * @return true if the state exists within the state machine.
	 */
	public boolean hasState(StateId id);

	/**
	 * Return the top level state machine. The top level state machine is
	 * the state machine that is not part of a composite state.
	 */
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
	 * Add a new state.
	 *
	 * Only one state with one ID is allowed. If another
	 * one with the same ID is attempted to be added, an exception is thrown.
	 *
	 * @param id The identifier of the new state.
	 * @return The instance of the new state.
	 * @throws DuplicateStateException If the state id already exists.
	 */
	public IState<StateId, Event> addState(StateId id);

	/**
	 * Add a new composits state.
	 *
	 * Only one state with one id is allowed.
	 * If another one with the same ID is attempted to be added, an
	 * exception is thrown.
	 *
	 * @param id The identifier of the new state.
	 * @return The instance of the new state.
	 * @throws DuplicateStateException If the state id already exists.
	 */
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