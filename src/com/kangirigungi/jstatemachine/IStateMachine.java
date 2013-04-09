package com.kangirigungi.jstatemachine;

import java.util.List;

public interface IStateMachine<StateId, Event> {
	public StateId getCurrentState();
	public List<StateId> getCurrentStates();
	public void processEvent(Event event);
}
