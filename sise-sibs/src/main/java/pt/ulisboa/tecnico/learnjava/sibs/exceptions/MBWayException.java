package pt.ulisboa.tecnico.learnjava.sibs.exceptions;

public class MBWayException extends Exception {
	private final String errorMessage;

	public MBWayException(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public MBWayException(String phoneNumber, String errorMessage) {
		this.errorMessage = "Friend " + phoneNumber + errorMessage;
	}

	public String getErrorMessage() {
		return this.errorMessage;
	}

}
