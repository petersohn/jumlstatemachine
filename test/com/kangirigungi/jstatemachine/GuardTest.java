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

import junit.framework.Assert;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import org.junit.Test;
import org.junit.Before;

public class GuardTest {
	private IGuard<Object, Object> trueGuard;
	private IGuard<Object, Object> falseGuard;

	@Before
	@SuppressWarnings("unchecked")
	public void preconditions() {
		trueGuard = mock(IGuard.class);
		when(trueGuard.checkTransition(
				any(IState.class), any(IState.class), any())).thenReturn(true);

		falseGuard = mock(IGuard.class);
		when(falseGuard.checkTransition(
				any(IState.class), any(IState.class), any())).thenReturn(false);
	}

	@Test
	public void guardNot() {
		GuardNot<Object, Object> guard = new GuardNot<Object, Object>(trueGuard);
		Assert.assertFalse(guard.checkTransition(null, null, null));

		guard = new GuardNot<Object, Object>(falseGuard);
		Assert.assertTrue(guard.checkTransition(null, null, null));
	}

	@Test
	public void guardAnd() {
		GuardAnd<Object, Object> guard = new GuardAnd<Object, Object>(
				falseGuard, falseGuard);
		Assert.assertFalse(guard.checkTransition(null, null, null));

		guard = new GuardAnd<Object, Object>(
				falseGuard, trueGuard);
		Assert.assertFalse(guard.checkTransition(null, null, null));

		guard = new GuardAnd<Object, Object>(
				trueGuard, falseGuard);
		Assert.assertFalse(guard.checkTransition(null, null, null));

		guard = new GuardAnd<Object, Object>(
				trueGuard, trueGuard);
		Assert.assertTrue(guard.checkTransition(null, null, null));
	}

	@Test
	public void guardOr() {
		GuardOr<Object, Object> guard = new GuardOr<Object, Object>(
				falseGuard, falseGuard);
		Assert.assertFalse(guard.checkTransition(null, null, null));

		guard = new GuardOr<Object, Object>(
				falseGuard, trueGuard);
		Assert.assertTrue(guard.checkTransition(null, null, null));

		guard = new GuardOr<Object, Object>(
				trueGuard, falseGuard);
		Assert.assertTrue(guard.checkTransition(null, null, null));

		guard = new GuardOr<Object, Object>(
				trueGuard, trueGuard);
		Assert.assertTrue(guard.checkTransition(null, null, null));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void guardState() {
		IStateMachineEngine<Integer, Integer> stateMachine =
				mock(IStateMachineEngine.class);
		IState<Integer, Integer> state1 = mock(IState.class);
		when(state1.getId()).thenReturn(1);
		IState<Integer, Integer> state2 = mock(IState.class);
		when(state2.getId()).thenReturn(2);
		IState<Integer, Integer> state3 = mock(IState.class);
		when(state3.getId()).thenReturn(3);

		GuardState<Integer, Integer> guard =
				new GuardState<Integer, Integer>(stateMachine,
						new Integer[] {1, 2}, false);

		Assert.assertFalse(guard.checkTransition(null, null, null));

		when(stateMachine.isActive()).thenReturn(true);

		when(stateMachine.getcurrentState()).thenReturn(state1);
		Assert.assertTrue(guard.checkTransition(null, null, null));
		when(stateMachine.getcurrentState()).thenReturn(state2);
		Assert.assertTrue(guard.checkTransition(null, null, null));
		when(stateMachine.getcurrentState()).thenReturn(state3);
		Assert.assertFalse(guard.checkTransition(null, null, null));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void guardStateDeep() {
		IStateMachineEngine<Integer, Integer> stateMachine =
				mock(IStateMachineEngine.class);
		IState<Integer, Integer> state1 = mock(IState.class);
		when(state1.getId()).thenReturn(1);
		IState<Integer, Integer> state2 = mock(IState.class);
		when(state2.getId()).thenReturn(2);
		IState<Integer, Integer> state3 = mock(IState.class);
		when(state3.getId()).thenReturn(3);

		GuardState<Integer, Integer> guard =
				new GuardState<Integer, Integer>(stateMachine,
						new Integer[] {1, 2}, true);

		Assert.assertFalse(guard.checkTransition(null, null, null));

		when(stateMachine.isActive()).thenReturn(true);

		when(stateMachine.getcurrentDeepState()).thenReturn(state1);
		Assert.assertTrue(guard.checkTransition(null, null, null));
		when(stateMachine.getcurrentDeepState()).thenReturn(state2);
		Assert.assertTrue(guard.checkTransition(null, null, null));
		when(stateMachine.getcurrentDeepState()).thenReturn(state3);
		Assert.assertFalse(guard.checkTransition(null, null, null));
	}
}
