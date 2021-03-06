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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import junit.framework.Assert;

import org.junit.Test;


public class CompositeStateTest {
	@Test
	public void legacyState() {
		MockStateFactory<Integer, Integer> mockStateFactory =
				new MockStateFactory<Integer, Integer>();
		CompositeState<Integer, Integer> compositeState =
				new CompositeState<Integer, Integer>(1, null, mockStateFactory);
		IState<Integer, Integer> state = mockStateFactory.lastCreatedState;
		IStateMachineEngine<Integer, Integer> stateMachineEngine =
				mockStateFactory.lastCreatedStateMachine;

		Assert.assertNotNull(mockStateFactory.lastCreatedState);
		Assert.assertEquals(new Integer(1), state.getId());
		verify(state, times(1)).getId();
		Assert.assertSame(stateMachineEngine, compositeState.getStateMachine());
		verifyNoMoreInteractions(state);
		verifyNoMoreInteractions(stateMachineEngine);

		compositeState.enterState(10);
		verify(state, times(1)).enterState(10);
		verify(stateMachineEngine, times(1)).enter();
		verifyNoMoreInteractions(state);
		verifyNoMoreInteractions(stateMachineEngine);

		compositeState.exitState(20);
		verify(state, times(1)).exitState(20);
		verify(stateMachineEngine, times(1)).leave();
		verifyNoMoreInteractions(state);
		verifyNoMoreInteractions(stateMachineEngine);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void stateMachine() {
		MockStateFactory<Integer, Integer> mockStateFactory =
				new MockStateFactory<Integer, Integer>();
		IStateMachineEngine<Integer, Integer> mockStateMachine =
				mock(IStateMachineEngine.class);
		CompositeState<Integer, Integer> compositeState =
				new CompositeState<Integer, Integer>(1,
						mockStateMachine, mockStateFactory);
		IStateMachineEngine<Integer, Integer> stateMachineEngine =
				mockStateFactory.lastCreatedStateMachine;

		Assert.assertSame(stateMachineEngine, compositeState.getStateMachine());
		Assert.assertSame(mockStateMachine,
				stateMachineEngine.getTopLevelStateMachine());
		verify(stateMachineEngine, times(1)).getTopLevelStateMachine();

		compositeState.processEvent(10);
		verify(stateMachineEngine, times(1)).processEvent(10);
		verifyNoMoreInteractions(stateMachineEngine);
	}
}
