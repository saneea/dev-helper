package io.github.saneea.feature;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Random;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

import io.github.saneea.Feature;
import io.github.saneea.FeatureContext;

public class RandomBytes implements Feature, Feature.CLI, Feature.Out.Bin.Stream {

	private static final String LENGTH = "length";
	private static final String SEED = "seed";

	private OutputStream out;

	private CommandLine commandLine;

	@Override
	public String getShortDescription() {
		return "generates random binary data";
	}

	@Override
	public void run(FeatureContext context) throws IOException {

		long size = Long.parseLong(commandLine.getOptionValue(LENGTH));

		String seed = commandLine.getOptionValue(SEED);

		Random r = seed == null //
				? new Random()
				: new Random(Long.parseLong(seed));

		byte[] bytes = new byte[1];

		for (int i = 0; i < size; ++i) {
			r.nextBytes(bytes);
			out.write(bytes);
		}
	}

	@Override
	public void setOut(OutputStream out) {
		this.out = out;
	}

	@Override
	public void setCommandLine(CommandLine commandLine) {
		this.commandLine = commandLine;
	}

	@Override
	public Option[] getOptions() {
		return new Option[] { //

				Option//
						.builder("l")//
						.longOpt(LENGTH)//
						.hasArg(true)//
						.argName("data size")//
						.required(true)//
						.desc("size of generated data in bytes")//
						.build(), //

				Option//
						.builder("s")//
						.longOpt(SEED)//
						.hasArg(true)//
						.argName("seed number")//
						.required(false)//
						.desc("seed for random generator")//
						.build()//
		};
	}

}
