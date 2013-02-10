package com.kangirigungi.jstatemachine;

public class NotRunningException extends StateMachineException {
	private static final long serialVersionUID = 1L;

	public NotRunningException() {
		super();
	}

	public NotRunningException(String message, Throwable cause) {
		super(message, cause);
	}

	public NotRunningException(String message) {
		super(message);
	}

	public NotRunningException(Throwable cause) {
		super(cause);
	}

}
