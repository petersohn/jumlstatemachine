package com.kangirigungi.jstatemachine;

import java.util.ArrayList;
import java.util.List;

class StateMachine<StateId, Event> implements IStateMachine<StateId, Event> {

	private IStateMachineEngine<StateId, Event> stateMachineEngine;

	public StateMachine(IStateMachineEngine<StateId, Event> stateMachineEngine) {
		this.stateMachineEngine = stateMachineEngine;
	}

	@Override
	public StateId getCurrentState() {
		return stateMachineEngine.getcurrentState().getId();
	}

	@Override
	public List<StateId> getCurrentStates() {
		ArrayList<StateId> result = new ArrayList<StateId>();

		IStateMachineEngine<StateId, Event> current = stateMachineEngine;

		while (true) {
			IState<StateId, Event> state = current.getcurrentState();
			result.add(state.getId());

			if (state instanceof ICompositeState<?, ?>) {
				current = ((ICompositeState<StateId, Event>)state).
						getStateMachine();
			} else {
				return result;
			}
		}
	}

	@Override
	public void processEvent(Event event) {
		stateMachineEngine.processEvent(event);
	}

}
