package com.kangirigungi.jstatemachine;

public class MockTransitionAction<StateId, Event> implements
		ITransitionAction<StateId, Event> {

	public boolean called = false;
	public IState<StateId, Event> fromState;
	public IState<StateId, Event> toState;
	public Event event;

	@Override
	public void onTransition(IState<StateId, Event> fromState,
			IState<StateId, Event> toState, Event event) {
		called = true;
		this.fromState = fromState;
		this.toState = toState;
		this.event = event;
	}
}
