package com.kangirigungi.jstatemachine;

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
	public boolean checkTransition(IState<StateId, Event> fromState,
			IState<StateId, Event> toState, Event event) {
		if (!stateMachine.isRunning()) {
			return false;
		}

		IState<StateId, Event> state = deep ?
				stateMachine.getcurrentDeepState() :
				stateMachine.getcurrentState();

		for (StateId stateId: states) {
			if (state.getId().equals(stateId)) {
				return true;
			}
		}
		return false;
	}



}
