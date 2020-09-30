package io.github.saneea;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import io.github.saneea.Feature.CLI.CommonOptions;

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

		try (InputStream input = new BufferedInputStream(System.in); //
				OutputStream output = new BufferedOutputStream(System.out)) {
			List<AutoCloseable> closeables = new ArrayList<>();

			try {
				if (feature instanceof Feature.CLI) {
					closeables.addAll(handleCLIParameterizedFeature((Feature.CLI) feature, featureName, args));
				}

				feature.run(new FeatureContext(args, input, output, System.err, appContext));

			} finally {
				closeAll(closeables);
			}
		}
	}

	private void closeAll(List<AutoCloseable> closeables) throws Exception {
		Exception onCloseException = null;
		for (AutoCloseable closeable : closeables) {
			try {
				closeable.close();
			} catch (Exception e) {
				if (onCloseException != null) {
					e.addSuppressed(onCloseException);
				}
				onCloseException = e;
			}
		}
		if (onCloseException != null) {
			throw onCloseException;
		}
	}

	private List<AutoCloseable> handleCLIParameterizedFeature(Feature.CLI feature, String featureName, String[] args)
			throws IOException {

		List<AutoCloseable> closeables = new ArrayList<>();

		Options cliOptions = new Options();
		for (Option option : feature.getOptions()) {
			cliOptions.addOption(option);
		}

		if (feature instanceof Feature.Out.Text.PrintStream//
				|| feature instanceof Feature.Out.Text.Writer) {
			cliOptions.addOption(CommonOptions.OUTPUT_ENCODING_OPTION);
		}

		if (feature instanceof Feature.In.Text.Reader//
				|| feature instanceof Feature.In.Text.String) {
			cliOptions.addOption(CommonOptions.INPUT_ENCODING_OPTION);
		}

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

		if (feature instanceof Feature.Out.Text.PrintStream) {
			Feature.Out.Text.PrintStream printStreamOutputable = (Feature.Out.Text.PrintStream) feature;

			String outputEncoding = commandLine.getOptionValue(CommonOptions.OUTPUT_ENCODING);

			OutputStream out = new BufferedOutputStream(System.out);

			PrintStream printStreamOut = outputEncoding == null//
					? new PrintStream(out)//
					: new PrintStream(out, false, outputEncoding);

			printStreamOutputable.setPrintStreamOut(printStreamOut);

			closeables.add(printStreamOut);
		}

		if (feature instanceof Feature.Out.Text.Writer) {
			Feature.Out.Text.Writer writerOutputable = (Feature.Out.Text.Writer) feature;

			String outputEncoding = commandLine.getOptionValue(CommonOptions.OUTPUT_ENCODING);

			OutputStream out = new BufferedOutputStream(System.out);

			Writer writerOut = outputEncoding == null//
					? new OutputStreamWriter(out)//
					: new OutputStreamWriter(out, outputEncoding);

			writerOutputable.setWriter(writerOut);

			closeables.add(writerOut);
		}

		if (feature instanceof Feature.Out.Bin.Stream) {
			Feature.Out.Bin.Stream outputStreamOutputable = (Feature.Out.Bin.Stream) feature;
			OutputStream out = new BufferedOutputStream(System.out);
			outputStreamOutputable.setOutputStreamOut(out);
			closeables.add(out);
		}

		if (feature instanceof Feature.In.Text.Reader) {
			Feature.In.Text.Reader readerInputtable = (Feature.In.Text.Reader) feature;

			String inputEncoding = commandLine.getOptionValue(CommonOptions.INPUT_ENCODING);

			Reader readerIn = inputEncoding == null//
					? new InputStreamReader(System.in)//
					: new InputStreamReader(System.in, inputEncoding);

			readerInputtable.setReader(readerIn);

			closeables.add(readerIn);
		}

		if (feature instanceof Feature.In.Text.String) {
			Feature.In.Text.String stringInputtable = (Feature.In.Text.String) feature;

			String inputEncoding = commandLine.getOptionValue(CommonOptions.INPUT_ENCODING);

			try (Reader readerIn = inputEncoding == null//
					? new InputStreamReader(System.in)//
					: new InputStreamReader(System.in, inputEncoding)) {

				StringWriter sw = new StringWriter();

				readerIn.transferTo(sw);

				stringInputtable.setString(sw.toString());
			}
		}

		if (feature instanceof Feature.In.Bin.Stream) {
			Feature.In.Bin.Stream inputStreamInputtable = (Feature.In.Bin.Stream) feature;
			InputStream in = System.in;
			inputStreamInputtable.setInputStream(in);
			closeables.add(in);
		}

		return closeables;
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
