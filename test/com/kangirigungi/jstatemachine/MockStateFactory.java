package com.kangirigungi.jstatemachine;

public class MockStateFactory<StateId, Event>
		implements IStateFactory<StateId, Event> {

	@Override
	public IState<StateId, Event> createState(StateId id) {
		return new MockState<StateId, Event>(id);
	}

	@Override
	public ICompositeState<StateId, Event> createCompositeState(
			StateId id, IStateMachine<StateId, Event> topLevelStateMachine) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IStateMachine<StateId, Event> createStateMachine(
		IStateMachine<StateId, Event> topLevelStateMachine) {
		// TODO Auto-generated method stub
		return null;
	}

}
