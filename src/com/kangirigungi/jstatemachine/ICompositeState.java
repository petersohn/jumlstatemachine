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
 * Represents a composite state. A composite state has a state machine of
 * its own that can react to events coming from the parent state machine
 * if the state is active. It uses the same set of states as the parent
 * state machine, but still each state needs to be unique among all levels
 * of a state hierarchy (i.e. a state present in a state machine cannot
 * be present in any of its composite states).
 *
 * The inner state machine of a composite state is running if and only if
 * the parent state machine is running and its active state is this composite
 * state. This means that calling the {@link IStateMachine#start() start()},
 * {@link IStateMachine#stop() stop()} or
 * {@link IStateMachine#processEvent(Object) processEvent()} methods of an
 * inner state machine results in undefined behavior.
 *
 * @author Peter Szabados
 *
 * @param <StateId> The type used for referencing states.
 * @param <Event> The type used for referencing events.
 */
public interface ICompositeState<StateId, Event>
		extends IState<StateId, Event> {
	/**
	 * Return the inner state machine associated with this composite state.
	 */
	public IStateMachine<StateId, Event> getStateMachine();

	@Override
	public ICompositeState<StateId, Event> setEntryExitAction(
			IEntryExitAction<StateId, Event> action);
}
