package com.kangirigungi.jstatemachine;

public interface IStateMachineBase<StateId, Event> {
	/**
	 *
	 * @return The current state of the state machine.
	 * @throws NotRunningException When the state machine is not running.
	 */
	public IState<StateId, Event> getcurrentState();

	/**
	 * Start the state machine. When the state machine is started, no more
	 * states or transitions can be added. The entry action of the initial
	 * state is executed.
	 * <p>
	 * If an exception is thrown from the entry action, then the state
	 * machine is not started.
	 *
	 * @throws AlreadyRunningException When the state machine is running.
	 */
	public void start();

	/**
	 * Stop the state machine. No more events can be triggered after the
	 * state machine is stopped. The exit action of the current state is
	 * executed.
	 * <p>
	 * If an exception is thrown from the exit action, then the state
	 * machine is not stopped.
	 */
	public void stop();

	/**
	 * @return True if the state machine is running.
	 */
	public boolean isRunning();

	/**
	 * Process an event and trigger any transitions needed to be done
	 * by the event. It must not be called from within callbacks. If
	 * called while it is already running, an exception is thrown.
	 * <p>
	 * The <code>null</code> value of event is special: it represents completion
	 * transitions. It is automatically processed after each transition.
	 * It can also be explicitly triggered (for example if there is a
	 * guarded completion transition and the guard value may have changed).
	 * <p>
	 * If an exception is thrown from a callback, the state machine doesn't
	 * change state. If the exit action of a state is already called (the
	 * exception is thrown from the action or the entry action of the next
	 * state) then the entry action of the original state is called again.
	 * After this, the exception is rethrown. No completion transitions
	 * are triggered after such exceptions.
	 *
	 * @param event The event to be processed.
	 * @throws StateMachineException for various cases of improper usage.
	 */
	public void processEvent(Event event);

}
