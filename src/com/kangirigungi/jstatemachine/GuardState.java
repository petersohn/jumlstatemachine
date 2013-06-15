package com.kangirigungi.jstatemachine;

import java.util.List;

public class GuardState<StateId, Event> implements IGuard<StateId, Event> {

	private IStateMachine<StateId, Event> stateMachine;
	private StateId[] states;
	private boolean deep;

	public GuardState(IStateMachine<StateId, Event> stateMachine,
			StateId[] states, boolean deep) {
		this.stateMachine = stateMachine;
		this.states = states.clone();
		this.deep = deep;
	}

	@Override
	public boolean checkTransition(StateId fromState,
			StateId toState, Event event) {

		List<StateId> currentStates = stateMachine.getCurrentStates();

		if (currentStates.size() == 0) {
			return false;
		}

		StateId currentState = currentStates.get(deep ?
				currentStates.size() - 1 : 0);

		for (StateId stateId: states) {
			if (currentState.equals(stateId)) {
				return true;
			}
		}

		return false;
	}



}
