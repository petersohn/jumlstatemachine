package com.kangirigungi.jstatemachine;

public interface ICompositeState<StateId, Event>
		extends IState<StateId, Event> {
	public IStateMachine<StateId, Event> getStateMachine();
}
