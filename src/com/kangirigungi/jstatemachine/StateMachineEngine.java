/*
 * Copyright (c) 2013, Peter Szabados
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 *     (1) Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *     (2) Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in
 *     the documentation and/or other materials provided with the
 *     distribution.
 *
 *     (3)The name of the author may not be used to
 *     endorse or promote products derived from this software without
 *     specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING
 * IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package com.kangirigungi.jstatemachine;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Basic implementation of {@link IStateMachineEngine}. It can be instantiated
 * directly for top level state machines.
 *
 * @author Peter Szabados
 *
 * @param <StateId> The type used for referencing states.
 * @param <Event> The type used for referencing events.
 */
class StateMachineEngine<StateId, Event> implements
		IStateMachineEngine<StateId, Event> {

	private static class TransitionTarget<StateId, Event> {
		public IGuard<StateId, Event> guard;
		public StateDescription<StateId, Event> targetState;
		public ITransitionAction<StateId, Event> action;

		public TransitionTarget(IGuard<StateId, Event> guard,
				StateDescription<StateId, Event> targetState,
				ITransitionAction<StateId, Event> action) {
			this.guard = guard;
			this.targetState = targetState;
			this.action = action;
		}
	}

	private static class StateDescription<StateId, Event> {
		public IState<StateId, Event> state;
		public Map<Event, Collection<TransitionTarget<StateId, Event>>> transitions;

		public StateDescription(IState<StateId, Event> state) {
			this.state = state;
			transitions = new HashMap<Event,
					Collection<TransitionTarget<StateId, Event>>>();
		}
	}

	private IStateFactory<StateId, Event> stateFactory
			= new StateFactory<StateId, Event>();;
	private Map<StateId, StateDescription<StateId, Event>> states
			= new HashMap<StateId, StateDescription<StateId, Event>>();
	private List<ICompositeState<StateId, Event>> substates =
			new ArrayList<ICompositeState<StateId, Event>>();
	private StateDescription<StateId, Event> initialState;
	private StateDescription<StateId, Event> currentState;
	private boolean inTransition = false;
	private IStateMachineEngine<StateId, Event> topLevelStateMachine = null;

	public StateMachineEngine() {
	}

	StateMachineEngine(IStateMachineEngine<StateId, Event> topLevelStateMachine) {
		this.topLevelStateMachine = topLevelStateMachine;
	}

	/* (non-Javadoc)
	 * @see com.kangirigungi.jstatemachine.IStateMachine#getInitialState()
	 */
	@Override
	public IState<StateId, Event> getInitialState() {
		return initialState.state;
	}

	/* (non-Javadoc)
	 * @see com.kangirigungi.jstatemachine.IStateMachine#getcurrentState()
	 */
	@Override
	public IState<StateId, Event> getcurrentState() {
		return currentState.state;
	}

	/* (non-Javadoc)
	 * @see com.kangirigungi.jstatemachine.IStateMachine#getcurrentState()
	 */
	@Override
	public IState<StateId, Event> getcurrentDeepState() {
		IState<StateId, Event> state = currentState.state;
		if (state instanceof ICompositeState<?, ?>) {
			return ((ICompositeState<StateId, Event>)state).getStateMachine().
					getcurrentDeepState();
		} else {
			return state;
		}

	}


	/* (non-Javadoc)
	 * @see com.kangirigungi.jstatemachine.IStateMachine#getState(StateId)
	 */
	@Override
	public IState<StateId, Event> getState(StateId id) {
		return getStateDescription(id).state;
	}

	/* (non-Javadoc)
	 * @see com.kangirigungi.jstatemachine.IStateMachine#setInitialState(StateId)
	 */
	@Override
	public void setInitialState(StateId initialState) {
		this.initialState = getStateDescription(initialState);
	}

	/* (non-Javadoc)
	 * @see com.kangirigungi.jstatemachine.IStateMachine#start()
	 */
	@Override
	public void enter() {
		initialState.state.enterState(null);
		currentState = initialState;

		checkedProcessEvent(null);
	}

	/* (non-Javadoc)
	 * @see com.kangirigungi.jstatemachine.IStateMachine#stop()
	 */
	@Override
	public void leave() {
		currentState.state.exitState(null);
		currentState = null;
	}

	/* (non-Javadoc)
	 * @see com.kangirigungi.jstatemachine.IStateMachine#isRunning()
	 */
	@Override
	public boolean isActive() {
		return currentState != null;
	}

	/* (non-Javadoc)
	 * @see com.kangirigungi.jstatemachine.IStateMachine#addState(StateId)
	 */
	@Override
	public IState<StateId, Event> addState(StateId id) {
		if (getTopLevelStateMachine().hasState(id)) {
			throw new DuplicateStateException(
					"Duplicate state: "+id.toString()+".",
					this, id);
		}

		IState<StateId, Event> state =
				stateFactory.createState(id);
		states.put(id, new StateDescription<StateId, Event>(state));
		return state;
	}

	@Override
	public ICompositeState<StateId, Event> addCompositeState(StateId id) {
		if (getTopLevelStateMachine().hasState(id)) {
			throw new DuplicateStateException(
					"Duplicate state: "+id.toString()+".",
					this, id);
		}

		ICompositeState<StateId, Event> state =
				stateFactory.createCompositeState(id,
						getTopLevelStateMachine());
		states.put(id, new StateDescription<StateId, Event>(state));
		substates.add(state);
		return state;
	}

	/* (non-Javadoc)
	 * @see com.kangirigungi.jstatemachine.IStateMachine#addTransition(StateId, Event, com.kangirigungi.jstatemachine.ITransitionAction, StateId, com.kangirigungi.jstatemachine.IGuard)
	 */
	@Override
	public void addTransition(StateId fromState, Event event,
			ITransitionAction<StateId, Event> action,
			StateId toState, IGuard<StateId, Event> guard) {
		StateDescription<StateId, Event> fromDescription =
				getStateDescription(fromState);
		StateDescription<StateId, Event> toDescription =
				getStateDescription(toState);

		doAddTransition(fromDescription, event, action,
				toDescription, guard);
	}

	/* (non-Javadoc)
	 * @see com.kangirigungi.jstatemachine.IStateMachine#addTransition(StateId, Event, com.kangirigungi.jstatemachine.ITransitionAction, StateId)
	 */
	@Override
	public void addTransition(StateId fromState, Event event,
			ITransitionAction<StateId, Event> action, StateId toState) {
		addTransition(fromState, event, action, toState, null);
	}

	/* (non-Javadoc)
	 * @see com.kangirigungi.jstatemachine.IStateMachine#addInternalTransition(StateId, Event, com.kangirigungi.jstatemachine.ITransitionAction, com.kangirigungi.jstatemachine.IGuard)
	 */
	@Override
	public void addInternalTransition(StateId state, Event event,
			ITransitionAction<StateId, Event> action,
			IGuard<StateId, Event> guard) {
		if (event == null) {
			throw new IllegalEventException("No internal completion " +
					"transitions are allowed.");
		}

		StateDescription<StateId, Event> description =
				getStateDescription(state);

		doAddTransition(description, event, action,
				null, guard);
	}

	/* (non-Javadoc)
	 * @see com.kangirigungi.jstatemachine.IStateMachine#addInternalTransition(StateId, Event, com.kangirigungi.jstatemachine.ITransitionAction)
	 */
	@Override
	public void addInternalTransition(StateId state, Event event,
			ITransitionAction<StateId, Event> action) {
		addInternalTransition(state, event, action, null);
	}

	private void doAddTransition(
			StateDescription<StateId, Event> fromDescription,
			Event event,
			ITransitionAction<StateId, Event> action,
			StateDescription<StateId, Event> toDescription,
			IGuard<StateId, Event> guard) {
		Collection<TransitionTarget<StateId, Event>> transitions =
				fromDescription.transitions.get(event);
		if (transitions == null) {
			transitions = new ArrayList<TransitionTarget<StateId, Event>>();
			fromDescription.transitions.put(event, transitions);
		} else {
			if (guard == null && !transitions.isEmpty()) {
				throwDuplicateTransitionException(
						fromDescription.state, event);
			} else {
				for (TransitionTarget<StateId, Event> transition:
					transitions) {
					if (transition.guard == null) {
						throwDuplicateTransitionException(
								fromDescription.state, event);
					}
				}
			}
		}
		transitions.add(new TransitionTarget<StateId, Event>(
				guard, toDescription, action));

	}

	/* (non-Javadoc)
	 * @see com.kangirigungi.jstatemachine.IStateMachine#processEvent(Event)
	 */
	@Override
	public void processEvent(Event event) {
		if (inTransition) {
			throw new InTransitionException("Cannot initiate transition " +
					"while another transition is running.");
		}

		checkedProcessEvent(event);
	}

	private void checkedProcessEvent(Event event) {
		inTransition = true;
		try {
			doProcessEvent(event);
		} finally {
			inTransition = false;
		}
	}

	private void doProcessEvent(Event event) {
		Collection<TransitionTarget<StateId, Event>> targets =
				currentState.transitions.get(event);
		if (targets != null) {
			for (TransitionTarget<StateId, Event> target: targets) {
				if (executeTransition(event, target)) {
					// process completion transitions
					doProcessEvent(null);
					break;
				}
			}
		} else {
			if (event != null) {
				// delegate the event
				currentState.state.processEvent(event);
			}
		}
	}

	private boolean executeTransition(Event event,
			TransitionTarget<StateId, Event> target) {
		IState<StateId, Event> targetState = target.targetState == null ?
				null : target.targetState.state;
		// check guard condition
		if (target.guard != null &&
				!target.guard.checkTransition(currentState.state,
						targetState, event)) {
			return false;
		}

		// execute transition
		if (target.targetState == null) {
			// internal transition
			if (target.action != null) {
				target.action.onTransition(currentState.state,
						null, event);
			}
			currentState.state.processEvent(event);
		} else {
			// change the state
			currentState.state.exitState(event);
			try {
				if (target.action != null) {
					target.action.onTransition(currentState.state,
							targetState, event);
				}
				target.targetState.state.enterState(event);
			} catch (RuntimeException e) {
				currentState.state.enterState(null);
				throw e;
			}
			currentState = target.targetState;
		}
		return true;
	}

	@Override
	public boolean hasState(StateId id) {
		for (ICompositeState<StateId, Event> substate: substates) {
			if (substate.getStateMachine().hasState(id)) {
				return true;
			}
		}
		return states.containsKey(id);
	}

	@Override
	public IStateMachineEngine<StateId, Event> getTopLevelStateMachine() {
		if (topLevelStateMachine == null) {
			return this;
		} else {
			return topLevelStateMachine;
		}
	}

	private StateDescription<StateId, Event> getStateDescription(StateId id) {
		StateDescription<StateId, Event> result = states.get(id);
		return result;
	}

	void setStateFactory(IStateFactory<StateId, Event> stateFactory) {
		this.stateFactory = stateFactory;
	}

}
