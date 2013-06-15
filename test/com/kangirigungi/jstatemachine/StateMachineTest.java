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

import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.ignoreStubs;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

public class StateMachineTest {

	private StateMachineEngine<Integer, Integer> stateMachine;
	private MockStateFactory<Integer, Integer> stateFactory;

	@Before
	public void initialize() {
		stateMachine = new StateMachineEngine<Integer, Integer>();
		stateFactory = new MockStateFactory<Integer, Integer>();
		stateMachine.setStateFactory(stateFactory);
	}

	@Test
	public void initialState() {
		System.out.println("initialState");
		stateMachine.addState(1);
		Assert.assertSame(stateFactory.lastCreatedState,
				stateMachine.getState(1));

		stateMachine.addState(2);
		Assert.assertSame(stateFactory.lastCreatedState,
				stateMachine.getState(2));

		stateMachine.setInitialState(1);
		Assert.assertSame(stateMachine.getState(1),
				stateMachine.getInitialState());

		Assert.assertFalse(stateMachine.isActive());
		stateMachine.enter();
		Assert.assertTrue(stateMachine.isActive());
		Assert.assertSame(stateMachine.getState(1),
				stateMachine.getcurrentState());

		Assert.assertTrue(stateMachine.hasState(1));
		Assert.assertTrue(stateMachine.hasState(2));
		Assert.assertFalse(stateMachine.hasState(4));

		verify(stateMachine.getState(1), times(1)).enterState(null);
		verifyNoMoreInteractions(ignoreStubs(stateMachine.getState(1)));
		verifyNoMoreInteractions(ignoreStubs(stateMachine.getState(2)));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void transition() {
		ITransitionAction<Integer, Integer> action =
				mock(ITransitionAction.class);

		System.out.println("transition");
		stateMachine.addState(1);
		stateMachine.addState(2);
		stateMachine.addTransition(1, 10, action, 2, null);
		stateMachine.setInitialState(1);
		stateMachine.enter();
		stateMachine.processEvent(10);

		Assert.assertSame(stateMachine.getState(2),
				stateMachine.getcurrentState());

		IState<Integer, Integer> state1 = stateMachine.getState(1);
		IState<Integer, Integer> state2 = stateMachine.getState(2);
		InOrder inOrder = inOrder(state1, state2, action);

		inOrder.verify(state1, times(1)).enterState(null);
		inOrder.verify(state1, times(1)).exitState(10);
		inOrder.verify(action, times(1)).onTransition(1, 2, 10);
		inOrder.verify(state2, times(1)).enterState(10);
		verifyNoMoreInteractions(ignoreStubs(state1));
		verifyNoMoreInteractions(ignoreStubs(state2));
		verifyNoMoreInteractions(action);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void backAndForthTransition() {
		ITransitionAction<Integer, Integer> action =
				mock(ITransitionAction.class);

		System.out.println("transition");
		stateMachine.addState(1);
		stateMachine.addState(2);
		stateMachine.addTransition(1, 10, action, 2, null);
		stateMachine.addTransition(2, 10, action, 1, null);
		stateMachine.setInitialState(1);
		stateMachine.enter();
		stateMachine.processEvent(10);
		stateMachine.processEvent(10);

		Assert.assertSame(stateMachine.getState(1),
				stateMachine.getcurrentState());

		IState<Integer, Integer> state1 = stateMachine.getState(1);
		IState<Integer, Integer> state2 = stateMachine.getState(2);
		InOrder inOrder = inOrder(state1, state2, action);

		inOrder.verify(state1, times(1)).enterState(null);
		inOrder.verify(state1, times(1)).exitState(10);
		inOrder.verify(action, times(1)).onTransition(1, 2, 10);
		inOrder.verify(state2, times(1)).enterState(10);
		inOrder.verify(state2, times(1)).exitState(10);
		inOrder.verify(action, times(1)).onTransition(2, 1, 10);
		inOrder.verify(state1, times(1)).enterState(10);
		verifyNoMoreInteractions(ignoreStubs(state1));
		verifyNoMoreInteractions(ignoreStubs(state2));
		verifyNoMoreInteractions(action);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void noTransition() {
		ITransitionAction<Integer, Integer> action =
				mock(ITransitionAction.class);

		System.out.println("noTransition");
		stateMachine.addState(1);
		stateMachine.addState(2);
		stateMachine.addTransition(1, 10, action, 2, null);
		stateMachine.setInitialState(1);
		stateMachine.enter();
		stateMachine.processEvent(20);

		Assert.assertSame(stateMachine.getState(1),
				stateMachine.getcurrentState());

		IState<Integer, Integer> state1 = stateMachine.getState(1);
		IState<Integer, Integer> state2 = stateMachine.getState(2);
		InOrder inOrder = inOrder(state1, state2, action);

		inOrder.verify(state1, times(1)).enterState(null);
		inOrder.verify(state1, times(1)).processEvent(20);
		verifyNoMoreInteractions(ignoreStubs(state1));
		verifyNoMoreInteractions(ignoreStubs(state2));
		verifyNoMoreInteractions(action);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void internalTransition() {
		ITransitionAction<Integer, Integer> action =
				mock(ITransitionAction.class);

		System.out.println("internalTransition");
		stateMachine.addState(1);
		stateMachine.addState(2);
		stateMachine.addTransition(1, 10, null, 2, null);
		stateMachine.addInternalTransition(1, 20, action, null);
		stateMachine.setInitialState(1);
		stateMachine.enter();
		stateMachine.processEvent(20);

		Assert.assertSame(stateMachine.getState(1),
				stateMachine.getcurrentState());

		IState<Integer, Integer> state1 = stateMachine.getState(1);
		IState<Integer, Integer> state2 = stateMachine.getState(2);
		InOrder inOrder = inOrder(state1, state2, action);

		inOrder.verify(state1, times(1)).enterState(null);
		inOrder.verify(action, times(1)).onTransition(1, null, 20);
		inOrder.verify(state1, times(1)).processEvent(20);
		verifyNoMoreInteractions(ignoreStubs(state1));
		verifyNoMoreInteractions(ignoreStubs(state2));
		verifyNoMoreInteractions(action);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void guardTransition() {
		FakeGuard<Integer, Integer> guard =
				new FakeGuard<Integer, Integer>(false);
		ITransitionAction<Integer, Integer> action =
				mock(ITransitionAction.class);

		System.out.println("guardInternalTransition");
		stateMachine.addState(1);
		stateMachine.addState(2);
		stateMachine.addTransition(1, 10, action, 2, guard);
		stateMachine.setInitialState(1);
		stateMachine.enter();
		stateMachine.processEvent(10);

		Assert.assertSame(stateMachine.getState(1),
				stateMachine.getcurrentState());

		IState<Integer, Integer> state1 = stateMachine.getState(1);
		IState<Integer, Integer> state2 = stateMachine.getState(2);
		InOrder inOrder = inOrder(state1, state2, action);

		inOrder.verify(state1, times(1)).enterState(null);
		verifyNoMoreInteractions(ignoreStubs(state1));
		verifyNoMoreInteractions(ignoreStubs(state2));
		verifyNoMoreInteractions(action);

		guard.setValue(true);
		stateMachine.processEvent(10);

		inOrder.verify(action, times(1)).onTransition(1, 2, 10);
		inOrder.verify(state2, times(1)).enterState(10);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void guardInternalTransition() {
		FakeGuard<Integer, Integer> guard =
				new FakeGuard<Integer, Integer>(false);
		ITransitionAction<Integer, Integer> action =
				mock(ITransitionAction.class);

		System.out.println("guardInternalTransition");
		stateMachine.addState(1);
		stateMachine.addInternalTransition(1, 10, action, guard);
		stateMachine.setInitialState(1);
		stateMachine.enter();
		stateMachine.processEvent(10);

		Assert.assertSame(stateMachine.getState(1),
				stateMachine.getcurrentState());

		IState<Integer, Integer> state = stateMachine.getState(1);
		InOrder inOrder = inOrder(state, action);

		inOrder.verify(state, times(1)).enterState(null);
		verifyNoMoreInteractions(ignoreStubs(state));
		verifyNoMoreInteractions(action);

		guard.setValue(true);
		stateMachine.processEvent(10);

		inOrder.verify(action, times(1)).onTransition(1, null, 10);
		inOrder.verify(state, times(1)).processEvent(10);
		verifyNoMoreInteractions(ignoreStubs(state));
		verifyNoMoreInteractions(action);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void completionTransition() {
		ITransitionAction<Integer, Integer> action =
				mock(ITransitionAction.class);

		System.out.println("completionTransition");
		stateMachine.addState(1);
		stateMachine.addState(2);
		stateMachine.addTransition(1, null, action, 2, null);
		stateMachine.setInitialState(1);
		stateMachine.enter();

		Assert.assertSame(stateMachine.getState(2),
				stateMachine.getcurrentState());

		IState<Integer, Integer> state1 = stateMachine.getState(1);
		IState<Integer, Integer> state2 = stateMachine.getState(2);
		InOrder inOrder = inOrder(state1, state2, action);

		inOrder.verify(state1, times(1)).enterState(null);
		inOrder.verify(state1, times(1)).exitState(null);
		inOrder.verify(action, times(1)).onTransition(1, 2, null);
		inOrder.verify(state2, times(1)).enterState(null);
		verifyNoMoreInteractions(ignoreStubs(state1));
		verifyNoMoreInteractions(ignoreStubs(state2));
		verifyNoMoreInteractions(action);
	}


	@Test
	public void stop() {
		System.out.println("stop");
		stateMachine.addState(1);
		stateMachine.setInitialState(1);
		Assert.assertFalse(stateMachine.isActive());

		IState<Integer, Integer> state = stateMachine.getState(1);
		InOrder inOrder = inOrder(state);

		stateMachine.enter();
		inOrder.verify(state, times(1)).enterState(null);
		verifyNoMoreInteractions(ignoreStubs(state));

		stateMachine.leave();
		inOrder.verify(state, times(1)).exitState(null);
		verifyNoMoreInteractions(ignoreStubs(state));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void processEventFromCallback() {
		System.out.println("processEventFromCallback");
		ITransitionAction<Integer, Integer> action =
				mock(ITransitionAction.class);
		doAnswer(new Answer<Object>() {
			@Override
			public Object answer(InvocationOnMock invocation) {
				stateMachine.processEvent(10);
				return null;
			}})
			.when(action).
			onTransition(anyInt(), anyInt(), anyInt());
		stateMachine.addState(1);
		stateMachine.addState(2);
		stateMachine.addTransition(1, 10, action, 2, null);
		stateMachine.setInitialState(1);
		stateMachine.enter();

		boolean exceptionThrown = false;
		try {
			stateMachine.processEvent(10);
		} catch (InTransitionException e) {
			exceptionThrown = true;
		}

		Assert.assertTrue(exceptionThrown);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void exceptionFromAction() {
		System.out.println("exceptionFromAction");
		ITransitionAction<Integer, Integer> action =
				mock(ITransitionAction.class);
		doThrow(new RuntimeException()).when(action).
			onTransition(anyInt(), anyInt(), anyInt());
		stateMachine.addState(1);
		stateMachine.addState(2);
		stateMachine.addTransition(1, 10, action, 2, null);
		stateMachine.setInitialState(1);
		stateMachine.enter();

		boolean exceptionThrown = false;
		try {
			stateMachine.processEvent(10);
		} catch (RuntimeException e) {
			exceptionThrown = true;
		}

		Assert.assertTrue(exceptionThrown);
		Assert.assertSame(stateMachine.getState(1), stateMachine.getcurrentState());

		IState<Integer, Integer> state1 = stateMachine.getState(1);
		IState<Integer, Integer> state2 = stateMachine.getState(2);
		InOrder inOrder = inOrder(state1, state2, action);

		inOrder.verify(state1, times(1)).enterState(null);
		inOrder.verify(state1, times(1)).exitState(10);
		inOrder.verify(action, times(1)).onTransition(1, 2, 10);
		inOrder.verify(state1, times(1)).enterState(null);
		verifyNoMoreInteractions(ignoreStubs(state1));
		verifyNoMoreInteractions(ignoreStubs(state2));
		verifyNoMoreInteractions(action);
	}
}
