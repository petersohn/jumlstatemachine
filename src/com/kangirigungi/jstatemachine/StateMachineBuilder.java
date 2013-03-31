package com.kangirigungi.jstatemachine;

public class StateMachineBuilder<StateId, Event> {
	private IStateMachineEngine<StateId, Event> stateMachineEngine;
	private SubStateMachineBuilder<StateId, Event> topLevelStateMachineBuilder;

	public SubStateMachineBuilder<StateId, Event> get() {
		return topLevelStateMachineBuilder;
	}

	public IStateMachine<StateId, Event> create() {
		return null;
	}


}
