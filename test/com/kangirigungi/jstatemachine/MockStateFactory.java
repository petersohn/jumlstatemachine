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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MockStateFactory<StateId, Event>
		implements IStateFactory<StateId, Event> {

	public IState<StateId, Event> lastCreatedState;
	public ICompositeState<StateId, Event> lastCreatedCompositeState;
	public IStateMachineEngine<StateId, Event> lastCreatedStateMachine;

	@Override
	@SuppressWarnings("unchecked")
	public IState<StateId, Event> createState(StateId id) {
		lastCreatedState = mock(IState.class);
		when(lastCreatedState.getId()).thenReturn(id);
		return lastCreatedState;
	}

	@Override
	@SuppressWarnings("unchecked")
	public ICompositeState<StateId, Event> createCompositeState(
			StateId id, IStateMachineEngine<StateId, Event> topLevelStateMachine) {
		lastCreatedCompositeState = mock(ICompositeState.class);
		when(lastCreatedCompositeState.getId()).thenReturn(id);
		when(lastCreatedCompositeState.getStateMachine()).
				thenReturn(topLevelStateMachine);
		return lastCreatedCompositeState;
	}

	@Override
	@SuppressWarnings("unchecked")
	public IStateMachineEngine<StateId, Event> createStateMachine(
		IStateMachineEngine<StateId, Event> topLevelStateMachine) {
		lastCreatedStateMachine = mock(IStateMachineEngine.class);
		when(lastCreatedStateMachine.getTopLevelStateMachine()).
				thenReturn(topLevelStateMachine);
		return lastCreatedStateMachine;
	}
}
