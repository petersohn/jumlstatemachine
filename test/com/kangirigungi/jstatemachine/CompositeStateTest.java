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

public class CompositeStateTest {
	@Test
	public void legacyState() {
		MockStateFactory<Integer, Integer> mockStateFactory =
				new MockStateFactory<Integer, Integer>();
		CompositeState<Integer, Integer> compositeState =
				new CompositeState<Integer, Integer>(1, null, mockStateFactory);

		Assert.assertNotNull(mockStateFactory.lastCreatedState);
		Assert.assertEquals(new Integer(1),
				mockStateFactory.lastCreatedState.getId());
		Assert.assertEquals(0, mockStateFactory.lastCreatedState.
				enterStateCalled);
		Assert.assertEquals(0, mockStateFactory.lastCreatedState.
				exitStateCalled);
		Assert.assertEquals(0, mockStateFactory.lastCreatedState.
				processEventCalled);

		compositeState.enterState(10);
		Assert.assertEquals(1, mockStateFactory.lastCreatedState.
				enterStateCalled);
		Assert.assertEquals(new Integer(10), mockStateFactory.lastCreatedState.
				enterStateEvent);

		compositeState.exitState(20);
		Assert.assertEquals(1, mockStateFactory.lastCreatedState.
				exitStateCalled);
		Assert.assertEquals(new Integer(20), mockStateFactory.lastCreatedState.
				exitStateEvent);
	}

	@Test
	public void stateMachine() {
		MockStateFactory<Integer, Integer> mockStateFactory =
				new MockStateFactory<Integer, Integer>();
		MockStateMachine<Integer, Integer> mockStateMachine =
				new MockStateMachine<Integer, Integer>();
		CompositeState<Integer, Integer> compositeState =
				new CompositeState<Integer, Integer>(1,
						mockStateMachine, mockStateFactory);

		Assert.assertSame(mockStateFactory.lastCreatedStateMachine,
				compositeState.getStateMachine());
		Assert.assertSame(mockStateMachine,
				mockStateFactory.lastCreatedStateMachine.topLevelStateMachine);
	}
}