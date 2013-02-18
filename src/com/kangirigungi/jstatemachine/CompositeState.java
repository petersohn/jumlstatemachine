package com.kangirigungi.jstatemachine;

public class CompositeState<StateId, Event>
		implements ICompositeState<StateId, Event> {

	IState<StateId, Event> state;
	private IStateMachine<StateId, Event> stateMachine;

	public CompositeState(StateId id,
			IStateMachine<StateId, Event> topLevelStateMachine,
			IStateFactory<StateId, Event> factory) {
		this.state = factory.createState(id);
		this.stateMachine = factory.createStateMachine(topLevelStateMachine);
	}

	@Override
	public void enterState(Event event) {
		state.enterState(event);

	}

	@Override
	public void exitState(Event event) {
		state.exitState(event);

	}

	@Override
	public void processEvent(Event event) {
		state.processEvent(event);
		stateMachine.processEvent(event);
	}

	@Override
	public StateId getId() {
		return state.getId();
	}

	@Override
	public IEntryExitAction<StateId, Event> getEntryExitAction() {
		return state.getEntryExitAction();
	}

	@Override
	public void setEntryExitAction(IEntryExitAction<StateId, Event> action) {
		state.setEntryExitAction(action);
	}

	@Override
	public IStateMachine<StateId, Event> getStateMachine() {
		return stateMachine;
	}

}
