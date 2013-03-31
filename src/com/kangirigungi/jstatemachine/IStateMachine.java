package com.kangirigungi.jstatemachine;

public interface IStateMachine<StateId, Event> {
	public StateId getCurrentState();
	public StateId[] getCurrentStates();
	public void processEvent(Event event);
}
