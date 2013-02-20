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

import org.junit.Before;
import org.junit.Test;

public class StateMachineTest {

	private StateMachine<Integer, Integer> stateMachine;

	@Before
	public void initialize() {
		stateMachine = new StateMachine<Integer, Integer>();
		stateMachine.setStateFactory(new MockStateFactory<Integer, Integer>());
	}

	@Test
	public void initialState() {
		System.out.println("initialState");
		stateMachine.addState(1);
		stateMachine.addState(2);
		stateMachine.setInitialState(1);
		Assert.assertSame(stateMachine.getState(1),
				stateMachine.getInitialState());

		Assert.assertFalse(stateMachine.isRunning());
		stateMachine.start();
		Assert.assertTrue(stateMachine.isRunning());
		Assert.assertSame(stateMachine.getState(1),
				stateMachine.getcurrentState());

		Assert.assertTrue(stateMachine.hasState(1));
		Assert.assertTrue(stateMachine.hasState(2));
		Assert.assertFalse(stateMachine.hasState(4));

		Assert.assertEquals(1, ((MockState<Integer, Integer>)stateMachine.
				getState(1)).enterStateCalled);
		Assert.assertSame(null, ((MockState<Integer, Integer>)stateMachine.
				getState(1)).enterStateEvent);
		Assert.assertEquals(0, ((MockState<Integer, Integer>)stateMachine.
				getState(1)).exitStateCalled);
		Assert.assertEquals(0, ((MockState<Integer, Integer>)stateMachine.
				getState(1)).processEventCalled);

		Assert.assertEquals(0, ((MockState<Integer, Integer>)stateMachine.
				getState(2)).enterStateCalled);
		Assert.assertEquals(0, ((MockState<Integer, Integer>)stateMachine.
				getState(2)).exitStateCalled);
		Assert.assertEquals(0, ((MockState<Integer, Integer>)stateMachine.
				getState(2)).processEventCalled);
	}

	@Test
	public void transition() {
		MockTransitionAction<Integer, Integer> action =
				new MockTransitionAction<Integer, Integer>();

		System.out.println("transition");
		stateMachine.addState(1);
		stateMachine.addState(2);
		stateMachine.addTransition(1, 10, action, 2);
		stateMachine.setInitialState(1);
		stateMachine.start();
		stateMachine.processEvent(10);

		Assert.assertSame(stateMachine.getState(2),
				stateMachine.getcurrentState());

		Assert.assertEquals(1, ((MockState<Integer, Integer>)stateMachine.
				getState(1)).enterStateCalled);
		Assert.assertSame(null, ((MockState<Integer, Integer>)stateMachine.
				getState(1)).enterStateEvent);
		Assert.assertEquals(1, ((MockState<Integer, Integer>)stateMachine.
				getState(1)).exitStateCalled);
		Assert.assertEquals(new Integer(10),
				((MockState<Integer, Integer>)stateMachine.getState(1)).
				exitStateEvent);
		Assert.assertEquals(0, ((MockState<Integer, Integer>)stateMachine.
				getState(1)).processEventCalled);

		Assert.assertEquals(1, ((MockState<Integer, Integer>)stateMachine.
				getState(2)).enterStateCalled);
		Assert.assertEquals(new Integer(10),
				((MockState<Integer, Integer>)stateMachine.getState(2)).
				enterStateEvent);
		Assert.assertEquals(0, ((MockState<Integer, Integer>)stateMachine.
				getState(2)).exitStateCalled);
		Assert.assertEquals(0, ((MockState<Integer, Integer>)stateMachine.
				getState(2)).processEventCalled);

		Assert.assertTrue(action.called);
		Assert.assertSame(stateMachine.getState(1), action.fromState);
		Assert.assertSame(stateMachine.getState(2), action.toState);
		Assert.assertEquals(new Integer(10), action.event);
	}

	@Test
	public void backAndForthTransition() {
		MockTransitionAction<Integer, Integer> action =
				new MockTransitionAction<Integer, Integer>();

		System.out.println("transition");
		stateMachine.addState(1);
		stateMachine.addState(2);
		stateMachine.addTransition(1, 10, action, 2);
		stateMachine.addTransition(2, 10, action, 1);
		stateMachine.setInitialState(1);
		stateMachine.start();
		stateMachine.processEvent(10);
		stateMachine.processEvent(10);

		Assert.assertSame(stateMachine.getState(1),
				stateMachine.getcurrentState());

		Assert.assertEquals(2, ((MockState<Integer, Integer>)stateMachine.
				getState(1)).enterStateCalled);
		Assert.assertEquals(new Integer(10),
				((MockState<Integer, Integer>)stateMachine.getState(1)).
				enterStateEvent);
		Assert.assertEquals(1,
				((MockState<Integer, Integer>)stateMachine.getState(1)).
				exitStateCalled);
		Assert.assertEquals(new Integer(10),
				((MockState<Integer, Integer>)stateMachine.getState(1)).
				exitStateEvent);
		Assert.assertEquals(0, ((MockState<Integer, Integer>)stateMachine.
				getState(1)).processEventCalled);

		Assert.assertEquals(1, ((MockState<Integer, Integer>)stateMachine.
				getState(2)).enterStateCalled);
		Assert.assertEquals(new Integer(10),
				((MockState<Integer, Integer>)stateMachine.getState(2)).
				enterStateEvent);
		Assert.assertEquals(1, ((MockState<Integer, Integer>)stateMachine.
				getState(2)).exitStateCalled);
		Assert.assertEquals(new Integer(10),
				((MockState<Integer, Integer>)stateMachine.getState(2)).
				exitStateEvent);
		Assert.assertEquals(0,
				((MockState<Integer, Integer>)stateMachine.getState(2)).
				processEventCalled);

		Assert.assertTrue(action.called);
		Assert.assertSame(stateMachine.getState(2), action.fromState);
		Assert.assertSame(stateMachine.getState(1), action.toState);
		Assert.assertEquals(new Integer(10), action.event);
	}

	@Test
	public void noTransition() {
		MockTransitionAction<Integer, Integer> action =
				new MockTransitionAction<Integer, Integer>();

		System.out.println("noTransition");
		stateMachine.addState(1);
		stateMachine.addState(2);
		stateMachine.addTransition(1, 10, action, 2);
		stateMachine.setInitialState(1);
		stateMachine.start();
		stateMachine.processEvent(20);

		Assert.assertSame(stateMachine.getState(1),
				stateMachine.getcurrentState());

		Assert.assertEquals(1, ((MockState<Integer, Integer>)stateMachine.
				getState(1)).enterStateCalled);
		Assert.assertSame(null, ((MockState<Integer, Integer>)stateMachine.
				getState(1)).enterStateEvent);
		Assert.assertEquals(0, ((MockState<Integer, Integer>)stateMachine.
				getState(1)).exitStateCalled);
		Assert.assertEquals(1, ((MockState<Integer, Integer>)stateMachine.
				getState(1)).processEventCalled);
		Assert.assertEquals(new Integer(20),
				((MockState<Integer, Integer>)stateMachine.
						getState(1)).processEventEvent);

		Assert.assertEquals(0, ((MockState<Integer, Integer>)stateMachine.
				getState(2)).enterStateCalled);
		Assert.assertEquals(0, ((MockState<Integer, Integer>)stateMachine.
				getState(2)).exitStateCalled);
		Assert.assertEquals(0, ((MockState<Integer, Integer>)stateMachine.
				getState(2)).processEventCalled);

		Assert.assertFalse(action.called);
	}

	@Test
	public void internalTransition() {
		MockTransitionAction<Integer, Integer> action =
				new MockTransitionAction<Integer, Integer>();

		System.out.println("internalTransition");
		stateMachine.addState(1);
		stateMachine.addState(2);
		stateMachine.addTransition(1, 10, null, 2);
		stateMachine.addInternalTransition(1, 20, action);
		stateMachine.setInitialState(1);
		stateMachine.start();
		stateMachine.processEvent(20);

		Assert.assertSame(stateMachine.getState(1),
				stateMachine.getcurrentState());

		Assert.assertEquals(1, ((MockState<Integer, Integer>)stateMachine.
				getState(1)).enterStateCalled);
		Assert.assertSame(null, ((MockState<Integer, Integer>)stateMachine.
				getState(1)).enterStateEvent);
		Assert.assertEquals(0, ((MockState<Integer, Integer>)stateMachine.
				getState(1)).exitStateCalled);
		Assert.assertEquals(1, ((MockState<Integer, Integer>)stateMachine.
				getState(1)).processEventCalled);

		Assert.assertEquals(0, ((MockState<Integer, Integer>)stateMachine.
				getState(2)).enterStateCalled);
		Assert.assertEquals(0, ((MockState<Integer, Integer>)stateMachine.
				getState(2)).exitStateCalled);
		Assert.assertEquals(0, ((MockState<Integer, Integer>)stateMachine.
				getState(2)).processEventCalled);

		Assert.assertTrue(action.called);
		Assert.assertSame(stateMachine.getState(1), action.fromState);
		Assert.assertSame(null, action.toState);
		Assert.assertEquals(new Integer(20), action.event);
	}

	@Test
	public void guardTransition() {
		MockGuard<Integer, Integer> guard =
				new MockGuard<Integer, Integer>(false);
		MockTransitionAction<Integer, Integer> action =
				new MockTransitionAction<Integer, Integer>();

		System.out.println("guardInternalTransition");
		stateMachine.addState(1);
		stateMachine.addState(2);
		stateMachine.addTransition(1, 10, action, 2, guard);
		stateMachine.setInitialState(1);
		stateMachine.start();
		stateMachine.processEvent(10);

		Assert.assertSame(stateMachine.getState(1),
				stateMachine.getcurrentState());

		Assert.assertEquals(1, ((MockState<Integer, Integer>)stateMachine.
				getState(1)).enterStateCalled);
		Assert.assertSame(null, ((MockState<Integer, Integer>)stateMachine.
				getState(1)).enterStateEvent);
		Assert.assertEquals(0, ((MockState<Integer, Integer>)stateMachine.
				getState(1)).processEventCalled);
		Assert.assertEquals(0, ((MockState<Integer, Integer>)stateMachine.
				getState(2)).enterStateCalled);
		Assert.assertEquals(0, ((MockState<Integer, Integer>)stateMachine.
				getState(2)).exitStateCalled);
		Assert.assertEquals(0, ((MockState<Integer, Integer>)stateMachine.
				getState(2)).processEventCalled);
		Assert.assertFalse(action.called);

		guard.setValue(true);
		stateMachine.processEvent(10);

		Assert.assertEquals(1, ((MockState<Integer, Integer>)stateMachine.
				getState(1)).exitStateCalled);
		Assert.assertEquals(0, ((MockState<Integer, Integer>)stateMachine.
				getState(1)).processEventCalled);
		Assert.assertEquals(1, ((MockState<Integer, Integer>)stateMachine.
				getState(2)).enterStateCalled);
		Assert.assertEquals(new Integer(10),
				((MockState<Integer, Integer>)stateMachine.getState(2)).
				enterStateEvent);
		Assert.assertEquals(0, ((MockState<Integer, Integer>)stateMachine.
				getState(2)).exitStateCalled);
		Assert.assertEquals(0, ((MockState<Integer, Integer>)stateMachine.
				getState(2)).processEventCalled);
		Assert.assertTrue(action.called);
		Assert.assertSame(stateMachine.getState(1), action.fromState);
		Assert.assertSame(stateMachine.getState(2), action.toState);
		Assert.assertEquals(new Integer(10), action.event);
	}

	@Test
	public void guardInternalTransition() {
		MockGuard<Integer, Integer> guard = new MockGuard<Integer, Integer>(false);
		MockTransitionAction<Integer, Integer> action =
				new MockTransitionAction<Integer, Integer>();

		System.out.println("guardInternalTransition");
		stateMachine.addState(1);
		stateMachine.addInternalTransition(1, 10, action, guard);
		stateMachine.setInitialState(1);
		stateMachine.start();
		stateMachine.processEvent(10);

		Assert.assertSame(stateMachine.getState(1),
				stateMachine.getcurrentState());

		Assert.assertEquals(1, ((MockState<Integer, Integer>)stateMachine.
				getState(1)).enterStateCalled);
		Assert.assertSame(null, ((MockState<Integer, Integer>)stateMachine.
				getState(1)).enterStateEvent);
		Assert.assertEquals(0, ((MockState<Integer, Integer>)stateMachine.
				getState(1)).processEventCalled);
		Assert.assertFalse(action.called);

		guard.setValue(true);
		stateMachine.processEvent(10);

		Assert.assertEquals(0, ((MockState<Integer, Integer>)stateMachine.
				getState(1)).exitStateCalled);
		Assert.assertEquals(1, ((MockState<Integer, Integer>)stateMachine.
				getState(1)).processEventCalled);
		Assert.assertSame(stateMachine.getState(1), action.fromState);
		Assert.assertSame(null, action.toState);
		Assert.assertEquals(new Integer(10), action.event);
	}

	@Test
	public void completionTransition() {
		MockTransitionAction<Integer, Integer> action =
				new MockTransitionAction<Integer, Integer>();

		System.out.println("completionTransition");
		stateMachine.addState(1);
		stateMachine.addState(2);
		stateMachine.addTransition(1, null, action, 2);
		stateMachine.setInitialState(1);
		stateMachine.start();

		Assert.assertSame(stateMachine.getState(2),
				stateMachine.getcurrentState());

		Assert.assertEquals(1, ((MockState<Integer, Integer>)stateMachine.
				getState(1)).enterStateCalled);
		Assert.assertSame(null, ((MockState<Integer, Integer>)stateMachine.
				getState(1)).enterStateEvent);
		Assert.assertEquals(1, ((MockState<Integer, Integer>)stateMachine.
				getState(1)).exitStateCalled);
		Assert.assertSame(null, ((MockState<Integer, Integer>)stateMachine.
				getState(1)).exitStateEvent);
		Assert.assertEquals(0, ((MockState<Integer, Integer>)stateMachine.
				getState(1)).processEventCalled);

		Assert.assertEquals(1, ((MockState<Integer, Integer>)stateMachine.
				getState(2)).enterStateCalled);
		Assert.assertSame(null, ((MockState<Integer, Integer>)stateMachine.
				getState(2)).enterStateEvent);
		Assert.assertEquals(0, ((MockState<Integer, Integer>)stateMachine.
				getState(2)).exitStateCalled);
		Assert.assertEquals(0, ((MockState<Integer, Integer>)stateMachine.
				getState(2)).processEventCalled);

		Assert.assertTrue(action.called);
		Assert.assertSame(stateMachine.getState(1), action.fromState);
		Assert.assertSame(stateMachine.getState(2), action.toState);
		Assert.assertSame(null, action.event);
	}


	@Test
	public void stop() {
		System.out.println("stop");
		stateMachine.addState(1);
		stateMachine.setInitialState(1);
		Assert.assertFalse(stateMachine.isRunning());

		stateMachine.start();
		Assert.assertTrue(stateMachine.isRunning());
		Assert.assertEquals(1, ((MockState<Integer, Integer>)stateMachine.
				getState(1)).enterStateCalled);
		Assert.assertSame(null, ((MockState<Integer, Integer>)stateMachine.
				getState(1)).enterStateEvent);
		Assert.assertEquals(0, ((MockState<Integer, Integer>)stateMachine.
				getState(1)).exitStateCalled);

		stateMachine.stop();
		Assert.assertFalse(stateMachine.isRunning());
		Assert.assertEquals(1, ((MockState<Integer, Integer>)stateMachine.
				getState(1)).enterStateCalled);
		Assert.assertSame(null, ((MockState<Integer, Integer>)stateMachine.
				getState(1)).enterStateEvent);
		Assert.assertEquals(1, ((MockState<Integer, Integer>)stateMachine.
				getState(1)).exitStateCalled);
		Assert.assertSame(null, ((MockState<Integer, Integer>)stateMachine.
				getState(1)).exitStateEvent);
	}

	@Test
	public void processEventFromCallback() {
		System.out.println("processEventFromCallback");
		ITransitionAction<Integer, Integer> action =
				new ITransitionAction<Integer, Integer>() {

			@Override
			public void onTransition(IState<Integer, Integer> fromState,
					IState<Integer, Integer> toState, Integer event) {
				stateMachine.processEvent(10);
			}
		};
		stateMachine.addState(1);
		stateMachine.addState(2);
		stateMachine.addTransition(1, 10, action, 2);
		stateMachine.setInitialState(1);
		stateMachine.start();

		boolean exceptionThrown = false;
		try {
			stateMachine.processEvent(10);
		} catch (InTransitionException e) {
			exceptionThrown = true;
		}

		Assert.assertTrue(exceptionThrown);
	}

	@Test
	public void exceptionFromAction() {
		System.out.println("exceptionFromAction");
		ITransitionAction<Integer, Integer> action =
				new ITransitionAction<Integer, Integer>() {

			@Override
			public void onTransition(IState<Integer, Integer> fromState,
					IState<Integer, Integer> toState, Integer event) {
				throw new RuntimeException();
			}
		};
		stateMachine.addState(1);
		stateMachine.addState(2);
		stateMachine.addTransition(1, 10, action, 2);
		stateMachine.setInitialState(1);
		stateMachine.start();

		boolean exceptionThrown = false;
		try {
			stateMachine.processEvent(10);
		} catch (RuntimeException e) {
			exceptionThrown = true;
		}

		Assert.assertTrue(exceptionThrown);
		Assert.assertSame(stateMachine.getState(1), stateMachine.getcurrentState());

		Assert.assertEquals(2, ((MockState<Integer, Integer>)stateMachine.
				getState(1)).enterStateCalled);
		Assert.assertSame(null, ((MockState<Integer, Integer>)stateMachine.
				getState(1)).enterStateEvent);
		Assert.assertEquals(1, ((MockState<Integer, Integer>)stateMachine.
				getState(1)).exitStateCalled);
		Assert.assertEquals(new Integer(10),
				((MockState<Integer, Integer>)stateMachine.getState(1)).
				exitStateEvent);

		Assert.assertEquals(0, ((MockState<Integer, Integer>)stateMachine.
				getState(2)).enterStateCalled);
		Assert.assertEquals(0, ((MockState<Integer, Integer>)stateMachine.
				getState(2)).exitStateCalled);
	}


}
