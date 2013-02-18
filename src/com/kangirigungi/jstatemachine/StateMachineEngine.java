package com.kangirigungi.jstatemachine;

public class StateMachineEngine<StateId, Event>
		implements IStateMachineEngine<StateId, Event> {

	IStateMachine<StateId, Event> stateMachine;

	StateMachineEngine() {

	}

	@Override
	public IState<StateId, Event> getcurrentState() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void start() {
		// TODO Auto-generated method stub

	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isRunning() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void processEvent(Event event) {
		// TODO Auto-generated method stub

	}

	@Override
	public IStateMachine<StateId, Event> getStateMachine() {
		// TODO Auto-generated method stub
		return null;
	}

}
