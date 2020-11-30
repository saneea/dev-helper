package io.github.saneea.dvh.feature.text;

import java.io.IOException;
import java.io.PrintStream;
import java.io.Reader;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

import io.github.saneea.dvh.Feature;
import io.github.saneea.dvh.FeatureContext;

public class Split implements//
		Feature, //
		Feature.CLI, //
		Feature.CLI.Options, //
		Feature.In.Text.Reader, //
		Feature.Out.Text.PrintStream {

	private static final int SIZE_OPT_DEFAULT = 60;
	private static final String SIZE_OPT = "size";

	private Reader in;
	private PrintStream out;
	private CommandLine commandLine;

	@Override
	public Meta meta(FeatureContext context) {
		return Meta.from("split text as lines");
	}

	@Override
	public void run(FeatureContext context) throws Exception {
		long size = getLineSize();
		while (transferLine(size)) {
			// no code
		}
	}

	private boolean transferLine(long size) throws IOException {
		for (long i = 0; i < size; ++i) {
			int charCode = in.read();

			if (charCode == -1) {
				if (i != 0) {
					out.println();
				}
				return false;
			}

			out.print((char) charCode);
		}
		out.println();

		return true;
	}

	private long getLineSize() {
		String sizeStr = commandLine.getOptionValue(SIZE_OPT);

		long size;
		if (sizeStr == null) {
			size = SIZE_OPT_DEFAULT;
		} else {
			size = Long.parseLong(sizeStr);
			if (size <= 0) {
				throw new IllegalArgumentException("Parameter " + SIZE_OPT + " must be greater than 0");
			}
		}

		return size;
	}

	@Override
	public void setIn(Reader in) {
		this.in = in;
	}

	@Override
	public void setOut(PrintStream out) {
		this.out = out;
	}

	@Override
	public void setCommandLine(CommandLine commandLine) {
		this.commandLine = commandLine;
	}

	@Override
	public Option[] getOptions() {
		Option[] options = { //
				Option//
						.builder("s")//
						.longOpt(SIZE_OPT)//
						.hasArg(true)//
						.argName("line size")//
						.required(false)//
						.desc("size limit for each line (default: " + SIZE_OPT_DEFAULT + ")")//
						.build()//
		};
		return options;
	}

}
