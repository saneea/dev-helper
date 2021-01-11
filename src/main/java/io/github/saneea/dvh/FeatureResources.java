package io.github.saneea.dvh;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
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
import java.util.stream.Stream;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;

import io.github.saneea.dvh.Feature.CLI.CommonOptions;
import io.github.saneea.dvh.Feature.Util.IOConsumer;
import io.github.saneea.dvh.utils.Utils;

public class FeatureResources implements AutoCloseable {

	private final Deque<AutoCloseable> closeables = new ArrayDeque<>();

	private final Feature feature;
	private final String[] args;
	private final FeatureContext context;

	private Options cliOptions;
	private CommandLine commandLine;
	private PrintStream outTextPrintStream;
	private Writer outTextWriter;
	private OutputStream outBinStream;
	private OutputStream errBinStream;
	private Reader inTextReader;
	private String inTextString;
	private InputStream inBinStream;

	public FeatureResources(Feature feature, String[] args, FeatureContext context) {
		this.feature = feature;
		this.args = args;
		this.context = context;
	}

	public Options getCliOptions() {
		if (cliOptions == null) {
			cliOptions = new Options();

			if (feature instanceof Feature.CLI.Options) {
				Stream//
						.of(//
								((Feature.CLI.Options) feature)//
										.getOptions())//
						.forEach(//
								cliOptions::addOption);
			}

			cliOptions.addOption(CommonOptions.HELP_OPTION);

			if (feature instanceof Feature.Out.Text.PrintStream//
					|| feature instanceof Feature.Out.Text.Writer//
					|| feature instanceof Feature.Out.Text.String) {
				cliOptions.addOption(CommonOptions.OUTPUT_ENCODING_OPTION);
			}

			if (feature instanceof Feature.In.Text.Reader//
					|| feature instanceof Feature.In.Text.String) {
				cliOptions.addOption(CommonOptions.INPUT_ENCODING_OPTION);
			}

			if (feature instanceof Feature.In.Bin.Stream//
					|| feature instanceof Feature.Out.Bin.Stream) {
				cliOptions.addOption(CommonOptions.NON_BUFFERED_STREAMS_OPTION);
			}

		}
		return cliOptions;
	}

	public CommandLine getCommandLine() {
		if (commandLine == null) {
			commandLine = Utils.parseCli(args, getCliOptions(), feature, context);
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
		String encodingName = getCommandLine().getOptionValue(encodingOptionName);
		return encodingName != null//
				? Charset.forName(encodingName)//
				: Charset.defaultCharset();
	}

	public OutputStream getOutBinStream() {
		if (outBinStream == null) {
			outBinStream = new FileOutputStream(FileDescriptor.out);

			if (useBufferedStreams()) {
				outBinStream = new BufferedOutputStream(outBinStream);
			}

			closeables.add(outBinStream);
		}
		return outBinStream;
	}

	public OutputStream getErrBinStream() {
		if (errBinStream == null) {
			errBinStream = new FileOutputStream(FileDescriptor.err);
			closeables.add(errBinStream);
		}
		return errBinStream;
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

	public Reader getInTextReader() throws IOException {
		if (inTextReader == null) {
			inTextReader = Utils.skipBom(//
					new InputStreamReader(//
							getInBinStream(), //
							getEncoding(CommonOptions.INPUT_ENCODING)));
			closeables.add(inTextReader);
		}
		return inTextReader;
	}

	public InputStream getInBinStream() {
		if (inBinStream == null) {
			inBinStream = new FileInputStream(FileDescriptor.in);

			if (useBufferedStreams()) {
				inBinStream = new BufferedInputStream(inBinStream);
			}

			closeables.add(inBinStream);
		}
		return inBinStream;
	}

	private boolean useBufferedStreams() {
		return !getCommandLine().hasOption(CommonOptions.NON_BUFFERED_STREAMS);
	}

	@Override
	public void close() throws Exception {
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

}