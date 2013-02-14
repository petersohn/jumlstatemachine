package com.kangirigungi.jstatemachine;

import junit.framework.Assert;

import org.junit.Test;

public class GuardTest {
	private static class ConstGuard implements IGuard<Object, Object> {
		private boolean value;
		
		public ConstGuard(boolean value) {
			this.value = value;
		}

		@Override
		public boolean checkTransition(IState<Object, Object> fromState,
				IState<Object, Object> toState, Object event) {
			return value;
		}
	}
	
	private IGuard<Object, Object> trueGuard = new ConstGuard(true);
	private IGuard<Object, Object> falseGuard = new ConstGuard(false);
	
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
}
