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
 * Exception thrown when a transaction with an already existing from
 * state and event to be added.
 *  
 * @author Peter Szabados
 * 
 */
public class DuplicateTransitionException extends StateMachineException {
	private static final long serialVersionUID = 1L;

	private StateMachine<?,?> stateMachine;
	private Object fromState;
	private Object event;

	public DuplicateTransitionException(StateMachine<?,?> stateMachine,
			Object fromState, Object event) {
		super();
		init(stateMachine, fromState, event);
	}

	public DuplicateTransitionException(String message, StateMachine<?,?> stateMachine,
			Object fromState, Object event) {
		super(message);
		init(stateMachine, fromState, event);
	}

	public DuplicateTransitionException(String message, Throwable cause,
			StateMachine<?,?> stateMachine,
			Object fromState, Object event) {
		super(message, cause);
		init(stateMachine, fromState, event);
	}

	public DuplicateTransitionException(Throwable cause, StateMachine<?,?> stateMachine,
			Object fromState, Object event) {
		super(cause);
		init(stateMachine, fromState, event);
	}

	private void init(StateMachine<?,?> stateMachine,
			Object fromState, Object event) {
		this.stateMachine = stateMachine;
		this.fromState = fromState;
		this.event = event;
	}

	public StateMachine<?, ?> getStateMachine() {
		return stateMachine;
	}

	public Object getFromState() {
		return fromState;
	}

	public Object getEvent() {
		return event;
	}

}
