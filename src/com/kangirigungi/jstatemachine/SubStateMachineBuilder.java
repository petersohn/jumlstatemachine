package com.kangirigungi.jstatemachine;

public class SubStateMachineBuilder<StateId, Event> {
	private IStateMachineEngine<StateId, Event> stateMachineEngine;

	SubStateMachineBuilder(
			IStateMachineEngine<StateId, Event> stateMachineEngine) {
		this.stateMachineEngine = stateMachineEngine;
	}

	public void setInitialState(StateId initialState) {
		stateMachineEngine.setInitialState(initialState);
	}

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
	public StateBuilder<StateId, Event> addState(StateId id) {
		return new StateBuilder<StateId, Event>(stateMachineEngine.addState(id));
	}

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
	 * @throws DuplicateTransitionException If there is an ambiguous transition.
	 * @throws {@link NoStateException} If either fromState of
	 * toState does not exist.
	 */
	public void addTransition(StateId fromState, Event event,
			ITransitionAction<StateId, Event> action, StateId toState,
			IGuard<StateId, Event> guard) {
		stateMachineEngine.addTransition(fromState, event, action, toState,
				guard);
	}

	/**
	 * Same as {@link #addTransition(Object, Object, ITransitionAction, Object, IGuard)
	 * addTransition(fromState, event, action, toState, null)}.
	 */
	public void addTransition(StateId fromState, Event event,
			ITransitionAction<StateId, Event> action, StateId toState) {
		stateMachineEngine.addTransition(fromState, event, action, toState, null);
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
	 * @throws DuplicateTransitionException If there is already a transition
	 * from the same state with the same event.
	 * @throws {@link NoStateException} If either fromState of
	 * toState does not exist.
	 */
	public void addInternalTransition(StateId state, Event event,
			ITransitionAction<StateId, Event> action,
			IGuard<StateId, Event> guard) {
		stateMachineEngine.addInternalTransition(state, event, action, guard);
	}

	/**
	 * Same as {@link #addInternalTransition(Object, Object, ITransitionAction, IGuard)
	 * addInternalTransition(state, event, action, null)}.
	 */
	public void addInternalTransition(StateId state, Event event,
			ITransitionAction<StateId, Event> action) {
		stateMachineEngine.addInternalTransition(state, event, action, null);
	}

}
