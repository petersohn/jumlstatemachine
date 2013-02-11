package com.kangirigungi.jstatemachine;

public interface IEntryExitAction<StateId, Event> {
	public void onEnter(IState<StateId, Event> state, Event event);
	public void onExit(IState<StateId, Event> state, Event event);
}
