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

	private static class MockState implements IState<Integer, Integer> {

		public Integer enterStateEvent;
		public Integer exitStateEvent;
		public Integer processEventEvent;
		public boolean enterStateCalled = false;
		public boolean exitStateCalled = false;
		public boolean processEventCalled = false;
		private Integer id;

		public MockState(Integer id) {
			this.id = id;
		}

		@Override
		public void enterState(Integer event) {
			System.out.println(id+": enterState("+event+")");
			enterStateCalled = true;
			enterStateEvent = event;
		}

		@Override
		public void exitState(Integer event) {
			System.out.println(id+": exitState("+event+")");
			exitStateCalled = true;
			exitStateEvent = event;
		}

		@Override
		public void processEvent(Integer event) {
			System.out.println(id+": processEvent("+event+")");
			processEventCalled = true;
			processEventEvent = event;
		}

		@Override
		public Integer getId() {
			return id;
		}

		@Override
		public IEntryExitAction<Integer, Integer> getEntryExitAction() {
			return null;
		}

		@Override
		public void setEntryExitAction(IEntryExitAction<Integer, Integer> action) {
		}

	}

	private static class MockStateFactory
			implements IStateFactory<Integer, Integer> {

		@Override
		public IState<Integer, Integer> createStete(
				StateMachine<Integer, Integer> stateMachine, Integer id) {
			return new MockState(id);
		}

	}
	
	private static class MockTransitionAction implements 
			ITransitionAction<Integer, Integer> {

		public boolean called = false;
		public IState<Integer, Integer> fromState;
		public IState<Integer, Integer> toState;
		public Integer event;

		@Override
		public void onTransition(IState<Integer, Integer> fromState,
				IState<Integer, Integer> toState, Integer event) {
			called = true;
			this.fromState = fromState;
			this.toState = toState;
			this.event = event;
		}
	}

	private StateMachine<Integer, Integer> stateMachine;

	@Before
	public void initialize() {
		stateMachine = new StateMachine<Integer, Integer>();
		stateMachine.setStateFactory(new MockStateFactory());
	}

	@Test
	public void initialState() {
		System.out.println("initialState");
		stateMachine.addState(1);
		stateMachine.addState(2);
		stateMachine.setInitialState(1);
		Assert.assertSame(stateMachine.getState(1), stateMachine.getInitialState());
		
		Assert.assertEquals(false, stateMachine.isRunning());
		stateMachine.start();
		Assert.assertEquals(true, stateMachine.isRunning());
		Assert.assertSame(stateMachine.getState(1), stateMachine.getcurrentState());

		Assert.assertEquals(true, ((MockState)stateMachine.getState(1)).enterStateCalled);
		Assert.assertSame(null, ((MockState)stateMachine.getState(1)).enterStateEvent);
		Assert.assertEquals(false, ((MockState)stateMachine.getState(1)).exitStateCalled);
		Assert.assertEquals(false, ((MockState)stateMachine.getState(1)).processEventCalled);

		Assert.assertEquals(false, ((MockState)stateMachine.getState(2)).enterStateCalled);
		Assert.assertEquals(false, ((MockState)stateMachine.getState(2)).exitStateCalled);
		Assert.assertEquals(false, ((MockState)stateMachine.getState(2)).processEventCalled);
	}

	@Test
	public void transition() {
		MockTransitionAction action = new MockTransitionAction();
		
		System.out.println("transition");
		stateMachine.addState(1);
		stateMachine.addState(2);
		stateMachine.addTransition(1, 10, action, 2);
		stateMachine.setInitialState(1);
		stateMachine.start();
		stateMachine.processEvent(10);

		Assert.assertSame(stateMachine.getState(2), stateMachine.getcurrentState());

		Assert.assertEquals(true, ((MockState)stateMachine.getState(1)).enterStateCalled);
		Assert.assertSame(null, ((MockState)stateMachine.getState(1)).enterStateEvent);
		Assert.assertEquals(true, ((MockState)stateMachine.getState(1)).exitStateCalled);
		Assert.assertEquals(new Integer(10), ((MockState)stateMachine.getState(1)).exitStateEvent);
		Assert.assertEquals(false, ((MockState)stateMachine.getState(1)).processEventCalled);

		Assert.assertEquals(true, ((MockState)stateMachine.getState(2)).enterStateCalled);
		Assert.assertEquals(new Integer(10), ((MockState)stateMachine.getState(2)).enterStateEvent);
		Assert.assertEquals(false, ((MockState)stateMachine.getState(2)).exitStateCalled);
		Assert.assertEquals(false, ((MockState)stateMachine.getState(2)).processEventCalled);
		
		Assert.assertEquals(true, action.called);
		Assert.assertSame(stateMachine.getState(1), action.fromState);
		Assert.assertSame(stateMachine.getState(2), action.toState);
		Assert.assertEquals(new Integer(10), action.event);
	}

	@Test
	public void noTransition() {
		MockTransitionAction action = new MockTransitionAction();
		
		System.out.println("noTransition");
		stateMachine.addState(1);
		stateMachine.addState(2);
		stateMachine.addTransition(1, 10, action, 2);
		stateMachine.setInitialState(1);
		stateMachine.start();
		stateMachine.processEvent(20);

		Assert.assertSame(stateMachine.getState(1), stateMachine.getcurrentState());

		Assert.assertEquals(true, ((MockState)stateMachine.getState(1)).enterStateCalled);
		Assert.assertSame(null, ((MockState)stateMachine.getState(1)).enterStateEvent);
		Assert.assertEquals(false, ((MockState)stateMachine.getState(1)).exitStateCalled);
		Assert.assertEquals(true, ((MockState)stateMachine.getState(1)).processEventCalled);
		Assert.assertEquals(new Integer(20), ((MockState)stateMachine.getState(1)).processEventEvent);

		Assert.assertEquals(false, ((MockState)stateMachine.getState(2)).enterStateCalled);
		Assert.assertEquals(false, ((MockState)stateMachine.getState(2)).exitStateCalled);
		Assert.assertEquals(false, ((MockState)stateMachine.getState(2)).processEventCalled);
		
		Assert.assertEquals(false, action.called);
	}
	
	@Test
	public void stop() {
		System.out.println("stop");
		stateMachine.addState(1);
		stateMachine.setInitialState(1);
		Assert.assertEquals(false, stateMachine.isRunning());
		
		stateMachine.start();
		Assert.assertEquals(true, stateMachine.isRunning());

		stateMachine.stop();
		Assert.assertEquals(false, stateMachine.isRunning());
	}


}
