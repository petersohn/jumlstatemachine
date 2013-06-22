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
 * Builder for composite states. It can be acquired by calling
 * {@link SubStateMachineBuilder#addCompositeState(Object)}. It differs from
 * {@link StateBuilder} in that it contains an own {@link SubStateMachineBuilder}
 * that can be used to build the sub state machine.
 */
public class CompositeStateBuilder<StateId, Event> {
	StateBuilder<StateId, Event> stateBuilder;
	SubStateMachineBuilder<StateId, Event> stateMachineBuilder;
	ICompositeState<StateId, Event> compositeState;

	CompositeStateBuilder(ICompositeState<StateId, Event> state) {
		stateBuilder = new StateBuilder<StateId, Event>(state);
		stateMachineBuilder = new SubStateMachineBuilder<StateId, Event>(
				state.getStateMachine());
		compositeState = state;
	}

	/**
	 * Get the id of the state.
	 */
	public StateId getId() {
		return stateBuilder.getId();
	}

	/**
	 * Get the callbacks that are called when the state is entered or exited.
	 * {@link #setEntryExitAction(IEntryExitAction) setEntryExitAction}
	 * method.
	 *
	 * @return The entry/exit action handler defined for this state.
	 */
	public IEntryExitAction<StateId, Event> getEntryExitAction() {
		return stateBuilder.getEntryExitAction();
	}

	/**
	 * Set the callbacks that are called when the state is entered or exited.
	 *
	 * @param action The entry/exit action handler defined for this state.
	 * @return this.
	 */
	public CompositeStateBuilder<StateId, Event> setEntryExitAction(
			IEntryExitAction<StateId, Event> action) {
		stateBuilder.setEntryExitAction(action);
		return this;
	}

	/**
	 * Get the state machine builder for this composite state.
	 */
	public SubStateMachineBuilder<StateId, Event> getStateMachineBuilder() {
		return stateMachineBuilder;
	}
}
