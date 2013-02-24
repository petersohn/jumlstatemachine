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

/**
 * Represents a state of the state machine. This is the basic
 * implementation of {@link IState}. Instantiation is done
 * by {@link StateMachine}.
 *
 * @author Peter Szabados
 *
 * @param <Id> The type used for referencing states.
 * @param <Event> The type used for referencing events.
 */
public class State<Id, Event> implements IState<Id, Event> {

	private Id id;
	private IEntryExitAction<Id, Event> entryExitAction;

	State(Id id) {
		this.id = id;
	}

	@Override
	public void enterState(Event event) {
		if (entryExitAction != null) {
			entryExitAction.onEnter(this, event);
		}
	}

	@Override
	public void exitState(Event event) {
		if (entryExitAction != null) {
			entryExitAction.onExit(this, event);
		}
	}

	@Override
	public void processEvent(Event event) {

	}

	@Override
	public Id getId() {
		return id;
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof IState)) {
			return false;
		}
		IState<?, ?> otherState = (IState<?, ?>)other;
		return getId().equals(otherState.getId());
	}

	@Override
	public int hashCode() {
		return getId().hashCode();
	}

	@Override
	public String toString() {
		return getId().toString();
	}

	@Override
	public IEntryExitAction<Id, Event> getEntryExitAction() {
		return entryExitAction;
	}

	@Override
	public IState<Id, Event> setEntryExitAction(
			IEntryExitAction<Id, Event> action) {
		entryExitAction = action;
		return this;
	}

}
