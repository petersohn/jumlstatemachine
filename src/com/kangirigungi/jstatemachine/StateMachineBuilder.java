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

import java.util.HashMap;

/**
 * Builder to create state machines. Use this to create instances of
 * {@link IStateMachine}. To create a state machine, follow these steps:
 * <ol>
 * <li>Create an instance of {@link StateMachineBuilder}.
 * <li>Use the {@link #get()} method to obtain an instance of
 * {@link SubStateMachineBuilder}.
 * <li>Use this instance to add states and transitions to the state machine.
 * <li>Each state has an instance of {@link StateBuilder} which can be
 * acquired by the {@link SubStateMachineBuilder#addState(Object)} method.
 * <li>Each composite state has an instance of {@link CompositeStateBuilder}
 * which can be acquired by the {@link SubStateMachineBuilder#addCompositeState(Object)}
 * method. Use {@link CompositeStateBuilder#getStateMachineBuilder()} to
 * acquire a {@link SubStateMachineBuilder} instance for the sub state machine.
 * <li>When all states and transitions of the state machine and all sub statates
 * are created, call {@link #create()} to create an instance of {@link IStateMachine}.
 * </ol>
 * <p>
 * Each state and event has a unique identifier represented by a respective Id types
 * (which are typically <code>enum</code> types, but they can be any type that can be
 * used as a key in a {@link HashMap}).
 * <p>
 * When a transition takes place in a state machine created by this class
 * (by calling the {@link IStateMachine#processEvent(Object)}
 * method), the actions are taken place in the following order:
 * <ol>
 * <li>The exit action of the old state is called.
 * <li>The transition action is called.
 * <li>The entry action of the new state is called.
 * <li>If the new state is a composite state, the entry action of the initial
 * state of the substate is called (possibly recursively for any subsequent
 * initial states).
 * is called.
 * <li>The current state is changed.
 * </ol>
 * For internal transition no exit or entry actions are called.
 * If an exception is thrown from within a callback, the state is changed.
 * If the exception is thrown after the exit action of the old state is
 * finished, the entry action of that state is called again.
 * <p>
 * <b>Note:</b> The created class (and the entire library) is not thread-safe.
 * This means that in order to use it from within multiple threads, calls to any
 * methods (typically {@link IStateMachine#processEvent(Object) processEvent})
 * must be synchronized.
 *
 * @author Peter Szabados
 *
 * @param <StateId> The type used for referencing states.
 * @param <Event> The type used for referencing events.
 */
public class StateMachineBuilder<StateId, Event> {
	private IStateMachineEngine<StateId, Event> stateMachineEngine;
	private SubStateMachineBuilder<StateId, Event> topLevelStateMachineBuilder;

	public StateMachineBuilder() {
		initialize();
	}

	/**
	 * Return the builder for the top level state machine.
	 */
	public SubStateMachineBuilder<StateId, Event> get() {
		return topLevelStateMachineBuilder;
	}

	/**
	 * Create the state machine. After calling this method, the created
	 * state machine is detached from this method. Calling {@link #get()}
	 * after this results in a builder for a completely new state machine.
	 *
	 * @return The created state machine.
	 */
	public IStateMachine<StateId, Event> create() {
		IStateMachine<StateId, Event> result =
				new StateMachine<StateId, Event>(stateMachineEngine);
		stateMachineEngine.enter();
		initialize();
		return result;
	}

	private void initialize() {
		stateMachineEngine = new StateMachineEngine<StateId, Event>();
		topLevelStateMachineBuilder =
				new SubStateMachineBuilder<StateId, Event>(stateMachineEngine);
	}
}
