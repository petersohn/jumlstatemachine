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
 * Interface representation of a state. Each state has a unique identifier
 * represented by an Id type (which is typically an <code>enum</code>). When referring
 * to states, this identifier is used in the state machine.
 * <p>
 * The actual implementation of the state is in the {@link State} class.
 *
 * @author Peter Szabados
 *
 * @param <Id> The type used for referencing states.
 * @param <Event> The type used for referencing events.
 * @see State
 */
public interface IState<Id, Event> {
	/**
	 * This method is called internally when the state is entered.
	 * Do not call this method from outside.
	 *
	 * @param event The event that triggers entering the state.
	 */
	public void enterState(Event event);

	/**
	 * This method is called internally when the state is exited.
	 * Do not call this method from outside.
	 *
	 * @param event The event that triggers exiting the state.
	 */
	public void exitState(Event event);

	/**
	 * This method is called internally when an event occurs but no
	 * state change is done. Do not call this method from outside.
	 *
	 * @param event The event that occurs.
	 */
	public void processEvent(Event event);

	/**
	 * @return the unique identifier of the state.
	 */
	public Id getId();

	/**
	 * Get the callbacks that are called when the state is entered or exited.
	 * {@link #setEntryExitAction(IEntryExitAction) setEntryExitAction}
	 * method.
	 *
	 * @return The entry/exit action handler defined for this state.
	 */
	public IEntryExitAction<Id, Event> getEntryExitAction();

	/**
	 * Set the callbacks that are called when the state is entered or exited.
	 *
	 * @param action The entry/exit action handler defined for this state.
	 * @return this.
	 */
	public IState<Id, Event> setEntryExitAction(
			IEntryExitAction<Id, Event> action);
}


