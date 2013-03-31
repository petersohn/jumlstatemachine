package com.kangirigungi.jstatemachine;

public class StateBuilder<StateId, Event> {
	private IState<StateId, Event> state;

	StateBuilder(IState<StateId, Event> state) {
		this.state = state;
	}

	public StateId getId() {
		return state.getId();
	}

	/**
	 * Get the callbacks that are called when the state is entered or exited.
	 * {@link #setEntryExitAction(IEntryExitAction) setEntryExitAction}
	 * method.
	 *
	 * @return The entry/exit action handler defined for this state.
	 */
	public IEntryExitAction<StateId, Event> getEntryExitAction() {
		return state.getEntryExitAction();
	}

	/**
	 * Set the callbacks that are called when the state is entered or exited.
	 *
	 * @param action The entry/exit action handler defined for this state.
	 * @return this.
	 */
	public StateBuilder<StateId, Event> setEntryExitAction(
			IEntryExitAction<StateId, Event> action) {
		state.setEntryExitAction(action);
		return this;
	}
}
