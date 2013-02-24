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

public class MockStateMachine<StateId, Event> implements
		IStateMachine<StateId, Event> {

	public IStateMachine<StateId, Event> topLevelStateMachine;
	public int startCalled = 0;
	public int stopCalled = 0;
	public int processEventCalled = 0;
	public Event lastEvent;

	public IState<StateId, Event> currentState;
	public ICompositeState<StateId, Event> currentDeepState;
	public boolean running = false;

	public MockStateMachine() {

	}

	public MockStateMachine(
			IStateMachine<StateId, Event> topLevelStateMachine) {
		this.topLevelStateMachine = topLevelStateMachine;
	}

	@Override
	public IState<StateId, Event> getInitialState() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IState<StateId, Event> getcurrentState() {
		return currentState;
	}

	@Override
	public IState<StateId, Event> getcurrentDeepState() {
		return currentDeepState;
	}

	@Override
	public IState<StateId, Event> getState(StateId id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasState(StateId id) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public IStateMachine<StateId, Event> getTopLevelStateMachine() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setInitialState(StateId initialState) {
		// TODO Auto-generated method stub

	}

	@Override
	public void start() {
		++startCalled;
	}

	@Override
	public void stop() {
		++stopCalled;
	}

	@Override
	public boolean isRunning() {
		return running;
	}

	@Override
	public IState<StateId, Event> addState(StateId id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ICompositeState<StateId, Event> addCompositeState(StateId id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addTransition(StateId fromState, Event event,
			ITransitionAction<StateId, Event> action, StateId toState,
			IGuard<StateId, Event> guard) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addTransition(StateId fromState, Event event,
			ITransitionAction<StateId, Event> action, StateId toState) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addInternalTransition(StateId state, Event event,
			ITransitionAction<StateId, Event> action,
			IGuard<StateId, Event> guard) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addInternalTransition(StateId state, Event event,
			ITransitionAction<StateId, Event> action) {
		// TODO Auto-generated method stub

	}

	@Override
	public void processEvent(Event event) {
		processEventCalled++;
		lastEvent = event;
	}


}
