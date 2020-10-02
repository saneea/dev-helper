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
import java.nio.charset.Charset;
import java.util.ArrayDeque;
import java.util.Deque;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import io.github.saneea.Feature.CLI;
import io.github.saneea.Feature.CLI.CommonOptions;
import io.github.saneea.Feature.Util.IOConsumer;

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
			FeatureResources featureResources = null;

			try {
				if (feature instanceof Feature.CLI) {
					featureResources = handleCLIParameterizedFeature((Feature.CLI) feature, featureName, args);
				}

				feature.run(new FeatureContext(args, input, output, System.err, appContext));

			} finally {
				if (featureResources != null) {
					featureResources.close();
				}
			}
		}
	}

	private static void closeAll(Deque<AutoCloseable> closeables) throws Exception {
		Exception onCloseException = null;

		while (!closeables.isEmpty()) {
			AutoCloseable closeable = closeables.pollLast();
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

	private FeatureResources handleCLIParameterizedFeature(Feature.CLI feature, String featureName, String[] args)
			throws IOException {

		FeatureResources featureResources = new FeatureResources(feature, featureName, args);

		CommandLine commandLine = featureResources.getCommandLine();

		feature.setCommandLine(commandLine);

		if (feature instanceof Feature.Out.Text.PrintStream) {
			((Feature.Out.Text.PrintStream) feature)//
					.setOut(//
							featureResources.getOutTextPrintStream());
		}

		if (feature instanceof Feature.Out.Text.Writer) {
			((Feature.Out.Text.Writer) feature)//
					.setOut(//
							featureResources.getOutTextWriter());
		}

		if (feature instanceof Feature.Out.Text.String) {
			((Feature.Out.Text.String) feature)//
					.setOut(//
							featureResources.getOutTextString());
		}

		if (feature instanceof Feature.Out.Bin.Stream) {
			((Feature.Out.Bin.Stream) feature)//
					.setOut(//
							featureResources.getOutBinStream());
		}

		if (feature instanceof Feature.In.Text.Reader) {
			((Feature.In.Text.Reader) feature)//
					.setIn(//
							featureResources.getInTextReader());
		}

		if (feature instanceof Feature.In.Text.String) {
			((Feature.In.Text.String) feature)//
					.setIn(//
							featureResources.getInTextString());
		}

		if (feature instanceof Feature.In.Bin.Stream) {
			((Feature.In.Bin.Stream) feature)//
					.setIn(//
							featureResources.getInBinStream());
		}

		return featureResources;
	}

	private static class FeatureResources implements AutoCloseable {

		private final Deque<AutoCloseable> closeables = new ArrayDeque<>();

		private final Feature.CLI feature;
		private final String featureName;
		private final String[] args;

		private Options cliOptions;
		private CommandLine commandLine;
		private PrintStream outTextPrintStream;
		private Writer outTextWriter;
		private OutputStream outBinStream;
		private Reader inTextReader;
		private String inTextString;
		private InputStream inBinStream;

		public FeatureResources(CLI feature, String featureName, String[] args) {
			this.feature = feature;
			this.featureName = featureName;
			this.args = args;
		}

		public Options getCliOptions() {
			if (cliOptions == null) {
				cliOptions = new Options();
				for (Option option : feature.getOptions()) {
					cliOptions.addOption(option);
				}

				if (feature instanceof Feature.Out.Text.PrintStream//
						|| feature instanceof Feature.Out.Text.Writer//
						|| feature instanceof Feature.Out.Text.String) {
					cliOptions.addOption(CommonOptions.OUTPUT_ENCODING_OPTION);
				}

				if (feature instanceof Feature.In.Text.Reader//
						|| feature instanceof Feature.In.Text.String) {
					cliOptions.addOption(CommonOptions.INPUT_ENCODING_OPTION);
				}

			}
			return cliOptions;
		}

		public CommandLine getCommandLine() {
			if (commandLine == null) {
				Options cliOptions = getCliOptions();

				CommandLineParser commandLineParser = new DefaultParser();

				try {
					commandLine = commandLineParser.parse(cliOptions, args);
				} catch (ParseException e) {
					System.err.println(e.getLocalizedMessage());
					new HelpFormatter().printHelp(featureName, cliOptions);
					throw new AppExitException(AppExitException.ExitCode.ERROR, e);
				}
			}
			return commandLine;
		}

		public IOConsumer<String> getOutTextString() {
			return getOutTextWriter()::write;
		}

		public Writer getOutTextWriter() {
			if (outTextWriter == null) {
				outTextWriter = new OutputStreamWriter(//
						getOutBinStream(), //
						getEncoding(CommonOptions.OUTPUT_ENCODING));
				closeables.add(outTextWriter);
			}
			return outTextWriter;
		}

		public PrintStream getOutTextPrintStream() {
			if (outTextPrintStream == null) {
				outTextPrintStream = new PrintStream(//
						getOutBinStream(), //
						false, //
						getEncoding(CommonOptions.OUTPUT_ENCODING));

				closeables.add(outTextPrintStream);
			}
			return outTextPrintStream;
		}

		private Charset getEncoding(String encodingOptionName) {
			String encodingName = commandLine.getOptionValue(encodingOptionName);
			return encodingName != null//
					? Charset.forName(encodingName)//
					: Charset.defaultCharset();
		}

		public OutputStream getOutBinStream() {
			if (outBinStream == null) {
				outBinStream = new BufferedOutputStream(System.out);
				closeables.add(outBinStream);
			}
			return outBinStream;
		}

		public String getInTextString() throws IOException {
			if (inTextString == null) {
				inTextString = readAll(getInTextReader());
			}
			return inTextString;
		}

		private static String readAll(Reader reader) throws IOException {
			StringWriter buf = new StringWriter();
			reader.transferTo(buf);
			return buf.toString();
		}

		public Reader getInTextReader() {
			if (inTextReader == null) {
				inTextReader = new InputStreamReader(//
						getInBinStream(), //
						getEncoding(CommonOptions.INPUT_ENCODING));
				closeables.add(inTextReader);
			}
			return inTextReader;
		}

		public InputStream getInBinStream() {
			if (inBinStream == null) {
				inBinStream = System.in;
				closeables.add(inBinStream);
			}
			return inBinStream;
		}

		@Override
		public void close() throws Exception {
			closeAll(closeables);
		}
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
