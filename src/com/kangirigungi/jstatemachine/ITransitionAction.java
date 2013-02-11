package com.kangirigungi.jstatemachine;

public interface ITransitionAction<StateId, Event> {
	public void onTransition(IState<StateId, Event> fromState, 
			IState<StateId, Event> toState, Event event);
}
