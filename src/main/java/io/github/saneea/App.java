package io.github.saneea;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import io.github.saneea.api.CLIParameterized;

public class App {

	private final AppContext appContext;

	private App() throws IOException {
		this.appContext = new AppContext();
	}

	public static void main(String[] args) throws Exception {
		try {
			String featureName = args.length != 0 ? args[0] : "help";

			App app = new App();
			app.run(featureName, withoutFeatureName(args));
		} catch (AppExitException appExitException) {
			System.exit(appExitException.getExitCode());
		}
	}

	private void run(String featureName, String[] args) throws Exception {
		Feature feature = appContext.createFeature(featureName);
		runFeature(feature, featureName, args);
	}

	private void runFeature(Feature feature, String featureName, String[] args) throws Exception {
		if (feature instanceof CLIParameterized) {
			handleCLIParameterizedFeature((CLIParameterized) feature, featureName, args);
		}

		try (InputStream input = new BufferedInputStream(System.in); //
				OutputStream output = new BufferedOutputStream(System.out)) {
			feature.run(new FeatureContext(args, input, output, System.err, appContext, featureName));
		}
	}

	private void handleCLIParameterizedFeature(CLIParameterized feature, String featureName, String[] args) {
		Options cliOptions = feature.createOptions();

		CommandLineParser commandLineParser = new DefaultParser();

		CommandLine commandLine;
		try {
			commandLine = commandLineParser.parse(cliOptions, args);
		} catch (ParseException e) {
			System.err.println(e.getLocalizedMessage());
			new HelpFormatter().printHelp(featureName, cliOptions);
			throw new AppExitException(AppExitException.ExitCode.ERROR, e);
		}

		feature.setCommandLine(commandLine);
	}

	private static String[] withoutFeatureName(String[] args) {
		String[] newArgs;
		if (args.length == 0) {
			newArgs = args;
		} else {
			newArgs = new String[args.length - 1];
			System.arraycopy(args, 1, newArgs, 0, newArgs.length);
		}
		return newArgs;
	}

	private static class AppExitException extends RuntimeException {

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
}
