package com.kangirigungi.jstatemachine;

public class CompositeStateBuilder<StateId, Event> {
	StateBuilder<StateId, Event> stateBuilder;
	SubStateMachineBuilder<StateId, Event> stateMachineBuilder;
	ICompositeState<StateId, Event> compositeState;

	CompositeStateBuilder(ICompositeState<StateId, Event> state) {
		stateBuilder = new StateBuilder<StateId, Event>(state);
		stateMachineBuilder = new SubStateMachineBuilder<StateId, Event>(
				state.getStateMachine());
		compositeState = state;
	}

	public StateId getId() {
		return stateBuilder.getId();
	}

	/**
	 * Get the callbacks that are called when the state is entered or exited.
	 * {@link #setEntryExitAction(IEntryExitAction) setEntryExitAction}
	 * method.
	 *
	 * @return The entry/exit action handler defined for this state.
	 */
	public IEntryExitAction<StateId, Event> getEntryExitAction() {
		return stateBuilder.getEntryExitAction();
	}

	/**
	 * Set the callbacks that are called when the state is entered or exited.
	 *
	 * @param action The entry/exit action handler defined for this state.
	 * @return this.
	 */
	public CompositeStateBuilder<StateId, Event> setEntryExitAction(
			IEntryExitAction<StateId, Event> action) {
		stateBuilder.setEntryExitAction(action);
		return this;
	}

	public SubStateMachineBuilder<StateId, Event> getStateMachineBuilder() {
		return stateMachineBuilder;
	}
}
