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

import java.util.List;

/**
 * Interface for a state machine. It represents a working state machine.
 * Use the {@link #processEvent(Object) processEvent} method to supply the
 * state machine with events. The action callbacks are called automatically
 * from within this method.
 * <p>
 * Do not derive directly from this class. Use {@link StateMachineBuilder#create()}
 * to acquire an implementation of this interface.
 */
public interface IStateMachine<StateId, Event> {
	/**
	 * Get the current state of the top level state machine.
	 */
	public StateId getCurrentState();
	/**
	 * Get a list of states that represents the current state of the state
	 * machine and all its substates. The first element of the result is
	 * the state of the top level state machine, and the last element is
	 * the state of the deepest substate.
	 */
	public List<StateId> getCurrentStates();

	/**
	 * Process one event of the state machine. Make any necessary state
	 * changes and call the action callbacks (transition, entry and exit
	 * actions).
	 *<p>
	 * <b>Note:</b> Do not call this method from within an action callback.
	 *
	 * @param event The event to be processed.
	 * @throws InTransitionException If a callback is called while a transition
	 * is taking place.
	 */
	public void processEvent(Event event);
}
