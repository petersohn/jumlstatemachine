package com.kangirigungi.jstatemachine;

public class MockCompositeState<StateId, Event>
		extends MockState<StateId, Event>
		implements ICompositeState<StateId, Event> {

	public IStateMachineEngine<StateId, Event> stateMachine;

	public MockCompositeState(StateId stateId) {
		super(stateId);
	}

	@Override
	public IStateMachineEngine<StateId, Event> getStateMachine() {
		return stateMachine;
	}

	@Override
	public ICompositeState<StateId, Event> setEntryExitAction(
			IEntryExitAction<StateId, Event> action) {
		return this;
	}

}
