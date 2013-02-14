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
 * Callback interface for guards. A guard action should only check conditions,
 * and have no side effects. When using guards to resolve event conflicts,
 * guards for all conflicting transitions must be mutually exclusive, and
 * it cannot be relied on that all guards are executed.
 * <p>
 * Logical functions of more than one guards can be created by instantiating
 * the {@link GuardAnd}, {@link GuardOr} and {@link GuardNot} classes.
 *
 * @author Peter Szabados
 *
 * @param <Id> The type used for referencing states.
 * @param <Event> The type used for referencing events.
 */
public interface IGuard<StateId, Event> {
	/**
	 * Called when the guard is needed to be executed.
	 *
	 * @param fromState The initial state of the transition. In case of an
	 * internal transition, it is the state in which the transition happens.
	 * @param toState The final state of the transition. In case of an
	 * internal transition, its value is null.
	 * @param event The event triggering the transition.
	 * @return The result of the transition.
	 */
	public boolean checkTransition(
			IState<StateId, Event> fromState,
			IState<StateId, Event> toState, Event event);
}
