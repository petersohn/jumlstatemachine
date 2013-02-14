package com.kangirigungi.jstatemachine;

class MockGuard<StateId, Event> implements IGuard<StateId, Event> {

	private boolean value;

	public MockGuard() {
		value = false;
	}

	public MockGuard(boolean value) {
		this.value = value;
	}

	@Override
	public boolean checkTransition(IState<StateId, Event> fromState,
			IState<StateId, Event> toState, Event event) {
		return value;
	}

	public boolean isValue() {
		return value;
	}

	public void setValue(boolean value) {
		this.value = value;
	}

}
