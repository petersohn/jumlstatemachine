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
 * Exception thrown when a state with an already existing id is attempted
 * to be added.
 *  
 * @author Peter Szabados
 * 
 */
public class DuplicateStateException extends StateMachineException {
	private static final long serialVersionUID = 1L;

	private IStateMachineEngine<?, ?> stateMachine;
	Object state;

	public DuplicateStateException(IStateMachineEngine<?, ?> stateMachine,
			Object state) {
		super();
		init(stateMachine, state);
	}

	public DuplicateStateException(String message, IStateMachineEngine<?, ?> stateMachine,
			Object state) {
		super(message);
		init(stateMachine, state);
	}

	public DuplicateStateException(String message, Throwable cause,
			IStateMachineEngine<?, ?> stateMachine,
			Object state) {
		super(message, cause);
		init(stateMachine, state);
	}

	public DuplicateStateException(Throwable cause, IStateMachineEngine<?, ?> stateMachine,
			Object state) {
		super(cause);
		init(stateMachine, state);
	}

	private void init(IStateMachineEngine<?, ?> stateMachine,
			Object state) {
		this.stateMachine = stateMachine;
		this.state = state;
	}

	public IStateMachineEngine<?, ?> getStateMachine() {
		return stateMachine;
	}

	public Object getState() {
		return state;
	}
}
