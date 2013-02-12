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

import org.junit.Test;


public class StateTest {

	private static class MockEntryExitAction 
			implements IEntryExitAction<Integer, Integer> {
		public IState<Integer, Integer> state;
		public int event;
		public boolean onEnterCalled = false;
		public boolean onExitCalled = false;
		
		@Override
		public void onEnter(IState<Integer, Integer> state, Integer event) {
			onEnterCalled = true;
			onExitCalled = false;
			this.state = state;
			this.event = event;
		}
		@Override
		public void onExit(IState<Integer, Integer> state, Integer event) {
			onExitCalled = true;
			onEnterCalled = false;
			this.state = state;
			this.event = event;
		}
	}
	
	@Test
	public void stateIdEquals() {
		State<Integer, Integer> state =
				new State<Integer, Integer>(null, 1);
		Assert.assertEquals(new Integer(1), state.getId());
		Assert.assertFalse(state.getId().equals(new Integer(2)));

		state = new State<Integer, Integer>(null, 42);
		Assert.assertEquals(new Integer(42), state.getId());
		Assert.assertFalse(state.getId().equals(new Integer(1)));
	}

	@Test
	public void stateEquals() {
		State<Integer, Integer> state1 =
				new State<Integer, Integer>(null, 1);
		State<Integer, Integer> state2 =
				new State<Integer, Integer>(null, 1);
		State<Integer, Integer> state3 =
				new State<Integer, Integer>(null, 2);
		Assert.assertEquals(state1, state2);
		Assert.assertEquals(state1.hashCode(), state2.hashCode());
		Assert.assertFalse(state1.equals(state3));
		Assert.assertFalse(state2.equals(state3));

		state1 = new State<Integer, Integer>(null, 42);
		state2 = new State<Integer, Integer>(null, 42);
		Assert.assertEquals(state1, state2);
		Assert.assertEquals(state1.hashCode(), state2.hashCode());
		Assert.assertFalse(state1.equals(state3));
		Assert.assertFalse(state2.equals(state3));
	}
	
	@Test
	public void entryExitAction() {
		State<Integer, Integer> state =
				new State<Integer, Integer>(null, 1);
		MockEntryExitAction action = new MockEntryExitAction();
		state.setEntryExitAction(action);
		Assert.assertSame(action, state.getEntryExitAction());
		Assert.assertEquals(false, action.onEnterCalled);
		Assert.assertEquals(false, action.onExitCalled);
		
		state.enterState(20);
		Assert.assertEquals(true, action.onEnterCalled);
		Assert.assertEquals(false, action.onExitCalled);
		Assert.assertSame(state, action.state);
		Assert.assertEquals(20, action.event);
		
		state.exitState(30);
		Assert.assertEquals(false, action.onEnterCalled);
		Assert.assertEquals(true, action.onExitCalled);
		Assert.assertSame(state, action.state);
		Assert.assertEquals(30, action.event);
	}

}
