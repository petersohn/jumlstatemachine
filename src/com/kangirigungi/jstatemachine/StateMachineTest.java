package com.kangirigungi.jstatemachine;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

public class StateMachineTest {

	private static class MockState implements IState<Integer, Integer> {

		public Integer enterStateEvent;
		public Integer exitStateEvent;
		public Integer processEventEvent;
		public boolean enterStateCalled;
		public boolean exitStateCalled;
		public boolean processEventCalled;
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

	}

	private static class MockStateFactory
			implements IStateFactory<Integer, Integer> {

		@Override
		public IState<Integer, Integer> createStete(
				StateMachine<Integer, Integer> stateMachine, Integer id) {
			return new MockState(id);
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
		
		stateMachine.start();
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
		System.out.println("transition");
		stateMachine.addState(1);
		stateMachine.addState(2);
		stateMachine.addTransition(1, 10, 2);
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
	}

	@Test
	public void noTransition() {
		System.out.println("noTransition");
		stateMachine.addState(1);
		stateMachine.addState(2);
		stateMachine.addTransition(1, 10, 2);
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
