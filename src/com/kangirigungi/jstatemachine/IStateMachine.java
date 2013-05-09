package com.kangirigungi.jstatemachine;

import java.util.List;

/**
 * Interface for a state machine. It represents a working state machine.
 * Use the {@link #processEvent(Object) processEvent} method to supply the
 * state machine with events. The action callbacks are called automatically
 * from within this method.
 * <p>
 * Do not derive directly from this class. Use {@link StateMachineBuilder#create()}
 * to acquire an implementation of this interface.
 */
public interface IStateMachine<StateId, Event> {
	/**
	 * Get the current state of the top level state machine.
	 */
	public StateId getCurrentState();
	/**
	 * Get a list of states that represents the current state of the state
	 * machine and all its substates. The first element of the result is
	 * the state of the top level state machine, and the last element is
	 * the state of the deepest substate.
	 */
	public List<StateId> getCurrentStates();

	/**
	 * Process one event of the state machine. Make any necessary state
	 * changes and call the action callbacks (transition, entry and exit
	 * actions).
	 *<p>
	 * <b>Note:</b> Do not call this method from within an action callback.
	 *
	 * @param event The event to be processed.
	 * @throws InTransitionException If a callback is called while a transition
	 * is taking place.
	 */
	public void processEvent(Event event);
}
