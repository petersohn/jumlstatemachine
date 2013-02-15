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
 * Callback interface for state entry and exit actions.
 *
 * @author Peter Szabados
 *
 * @param <StateId> The type used for referencing states.
 * @param <Event> The type used for referencing events.
 */
public interface IEntryExitAction<StateId, Event> {
	/**
	 * Called when the state is entered. The event parameter contains
	 * the event by which the state is entered, or null in the following
	 * situations:
	 * <ul>
	 * <li>Entering the initial state of a state machine when calling
	 * {@link StateMachine#start() start()}.
	 * <li>Entering the state through a completion transition.
	 * <li>An exception is thrown from an action callback and the original
	 * state is being reentered.
	 * </ul>
	 *
	 * @param state The state being entered.
	 * @param event The event triggering the transition into the state.
	 */
	public void onEnter(IState<StateId, Event> state, Event event);

	/**
	 * Called when the state is exited.
	 * @param state The state being exited.
	 * @param event The event triggering the transition from the state.
	 */
	public void onExit(IState<StateId, Event> state, Event event);
}
