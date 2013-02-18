package com.kangirigungi.jstatemachine;

public interface IStateMachineEngine<StateId, Event> extends IStateMachineBase<StateId, Event> {
	IStateMachine<StateId, Event> getStateMachine();
}
