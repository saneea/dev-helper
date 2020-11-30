package io.github.saneea.dvh;

public class AppExitException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public interface ExitCode {
		int OK = 0;
		int ERROR = 1;
	}

	private final int exitCode;

	public AppExitException(int exitCode) {
		this(exitCode, null);
	}

	public AppExitException(int exitCode, Throwable cause) {
		super(cause);
		this.exitCode = exitCode;
	}

	public int getExitCode() {
		return exitCode;
	}
}