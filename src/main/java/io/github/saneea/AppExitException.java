package io.github.saneea;

public class AppExitException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	interface ExitCode {
		int ERROR = 1;
	}

	private final int exitCode;

	public AppExitException(int exitCode, Throwable cause) {
		super(cause);
		this.exitCode = exitCode;
	}

	public int getExitCode() {
		return exitCode;
	}
}