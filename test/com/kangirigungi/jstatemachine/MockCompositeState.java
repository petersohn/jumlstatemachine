package com.kangirigungi.jstatemachine;

public class MockCompositeState<StateId, Event>
		extends MockState<StateId, Event>
		implements ICompositeState<StateId, Event> {

	public IStateMachine<StateId, Event> stateMachine;

	public MockCompositeState(StateId stateId) {
		super(stateId);
	}

	@Override
	public IStateMachine<StateId, Event> getStateMachine() {
		return stateMachine;
	}

}
