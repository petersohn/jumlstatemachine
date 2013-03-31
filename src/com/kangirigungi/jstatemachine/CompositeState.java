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

/**
 * Implementation of {@link ICompositeState}. It instantiates its own
 * inner state machine, which is returned by {@link #getStateMachine()}.
 *
 * @author Peter Szabados
 *
 * @param <StateId> The type used for referencing states.
 * @param <Event> The type used for referencing events.
 */
public class CompositeState<StateId, Event>
		implements ICompositeState<StateId, Event> {

	IState<StateId, Event> state;
	private IStateMachineEngine<StateId, Event> stateMachine;

	public CompositeState(StateId id,
			IStateMachineEngine<StateId, Event> topLevelStateMachine,
			IStateFactory<StateId, Event> factory) {
		this.state = factory.createState(id);
		this.stateMachine = factory.createStateMachine(topLevelStateMachine);
	}

	@Override
	public void enterState(Event event) {
		state.enterState(event);
		stateMachine.enter();
	}

	@Override
	public void exitState(Event event) {
		stateMachine.leave();
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
	public void setEntryExitAction(
			IEntryExitAction<StateId, Event> action) {
		state.setEntryExitAction(action);
	}

	@Override
	public IStateMachineEngine<StateId, Event> getStateMachine() {
		return stateMachine;
	}

}
