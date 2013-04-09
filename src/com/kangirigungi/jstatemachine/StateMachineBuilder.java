package com.kangirigungi.jstatemachine;

public class StateMachineBuilder<StateId, Event> {
	private IStateMachineEngine<StateId, Event> stateMachineEngine;
	private SubStateMachineBuilder<StateId, Event> topLevelStateMachineBuilder;

	public StateMachineBuilder() {
		initialize();
	}

	public SubStateMachineBuilder<StateId, Event> get() {
		return topLevelStateMachineBuilder;
	}

	public IStateMachine<StateId, Event> create() {
		IStateMachine<StateId, Event> result =
				new StateMachine<StateId, Event>(stateMachineEngine);
		initialize();
		return result;
	}

	private void initialize() {
		stateMachineEngine = new StateMachineEngine<StateId, Event>();
		topLevelStateMachineBuilder =
				new SubStateMachineBuilder<StateId, Event>(stateMachineEngine);
	}
}
