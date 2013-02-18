package com.kangirigungi.jstatemachine;

public interface IStateMachine<StateId, Event> extends IStateMachineBase<StateId, Event> {

	/**
	 * @return The initial state of the state machine.
	 */
	public IState<StateId, Event> getInitialState();

	/**
	 * Get the state associated with the given id. If the state does not
	 * exist, an exception is thrown. States are added by the
	 * {@link #addState(Object) addState} method.
	 * @param id The identifier of the state.
	 * @return The state.
	 */
	public IState<StateId, Event> getState(StateId id);

	/**
	 * Set the initial state for the state machine. This must always
	 * be called before starting the state machine.
	 * @param initialState The new initial state.
	 * @throws AlreadyRunningException When the state machine is running.
	 */
	public void setInitialState(StateId initialState);

	/**
	 * Add a new state. Only one state with one id is allowed. If another
	 * one with the same idis attempted to be added, an exception is thrown.
	 *
	 * @param id The identifier of the new state.
	 * @return The instance of the new state.
	 * @throws DuplicateStateException If the state id already exists.
	 */
	public IState<StateId, Event> addState(StateId id);

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

}