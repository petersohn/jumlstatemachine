package com.kangirigungi.jstatemachine;

public class MockState<StateId, Event> implements IState<StateId, Event> {

	public Event enterStateEvent;
	public Event exitStateEvent;
	public Event processEventEvent;
	public int enterStateCalled = 0;
	public int exitStateCalled = 0;
	public int processEventCalled = 0;
	private StateId id;

	public MockState(StateId id) {
		this.id = id;
	}

	@Override
	public void enterState(Event event) {
		System.out.println(id+": enterState("+event+")");
		++enterStateCalled;
		enterStateEvent = event;
	}

	@Override
	public void exitState(Event event) {
		System.out.println(id+": exitState("+event+")");
		++exitStateCalled;
		exitStateEvent = event;
	}

	@Override
	public void processEvent(Event event) {
		System.out.println(id+": processEvent("+event+")");
		++processEventCalled;
		processEventEvent = event;
	}

	@Override
	public StateId getId() {
		return id;
	}

	@Override
	public IEntryExitAction<StateId, Event> getEntryExitAction() {
		return null;
	}

	@Override
	public void setEntryExitAction(IEntryExitAction<StateId, Event> action) {
	}

}