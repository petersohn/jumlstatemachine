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

import java.util.Arrays;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

public class GuardTest {
	private IGuard<Object, Object> trueGuard;
	private IGuard<Object, Object> falseGuard;

	@Before
	public void preconditions() {
		trueGuard = new FakeGuard<Object, Object>(true);

		falseGuard = new FakeGuard<Object, Object>(false);
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
		IStateMachine<Integer, Integer> stateMachine =
				mock(IStateMachine.class);

		GuardState<Integer, Integer> guard =
				new GuardState<Integer, Integer>(stateMachine,
						new Integer[] {1, 2}, false);

		Assert.assertFalse(guard.checkTransition(null, null, null));

		when(stateMachine.getCurrentStates()).thenReturn(Arrays.asList(1));
		Assert.assertTrue(guard.checkTransition(null, null, null));
		when(stateMachine.getCurrentStates()).thenReturn(Arrays.asList(2));
		Assert.assertTrue(guard.checkTransition(null, null, null));
		when(stateMachine.getCurrentStates()).thenReturn(Arrays.asList(3));
		Assert.assertFalse(guard.checkTransition(null, null, null));
		when(stateMachine.getCurrentStates()).thenReturn(Arrays.asList(1, 5));
		Assert.assertTrue(guard.checkTransition(null, null, null));
		when(stateMachine.getCurrentStates()).thenReturn(Arrays.asList(2, 6));
		Assert.assertTrue(guard.checkTransition(null, null, null));
		when(stateMachine.getCurrentStates()).thenReturn(Arrays.asList(3, 1));
		Assert.assertFalse(guard.checkTransition(null, null, null));
		when(stateMachine.getCurrentStates()).thenReturn(Arrays.asList(4, 2));
		Assert.assertFalse(guard.checkTransition(null, null, null));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void guardStateDeep() {
		IStateMachine<Integer, Integer> stateMachine =
				mock(IStateMachine.class);

		GuardState<Integer, Integer> guard =
				new GuardState<Integer, Integer>(stateMachine,
						new Integer[] {1, 2}, true);

		Assert.assertFalse(guard.checkTransition(null, null, null));

		when(stateMachine.getCurrentStates()).thenReturn(Arrays.asList(1));
		Assert.assertTrue(guard.checkTransition(null, null, null));
		when(stateMachine.getCurrentStates()).thenReturn(Arrays.asList(2));
		Assert.assertTrue(guard.checkTransition(null, null, null));
		when(stateMachine.getCurrentStates()).thenReturn(Arrays.asList(3));
		Assert.assertFalse(guard.checkTransition(null, null, null));
		when(stateMachine.getCurrentStates()).thenReturn(Arrays.asList(1, 5));
		Assert.assertFalse(guard.checkTransition(null, null, null));
		when(stateMachine.getCurrentStates()).thenReturn(Arrays.asList(2, 6));
		Assert.assertFalse(guard.checkTransition(null, null, null));
		when(stateMachine.getCurrentStates()).thenReturn(Arrays.asList(3, 1));
		Assert.assertTrue(guard.checkTransition(null, null, null));
		when(stateMachine.getCurrentStates()).thenReturn(Arrays.asList(4, 2));
		Assert.assertTrue(guard.checkTransition(null, null, null));
	}
}
