package org.snipcloud.core;

public class RegistrationError extends Exception {
	private static final long serialVersionUID = 5247400979642619215L;

	public RegistrationError(String message) {
		super(message);
	}
}
