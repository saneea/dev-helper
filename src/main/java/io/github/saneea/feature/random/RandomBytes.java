package io.github.saneea.feature.random;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Random;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

import io.github.saneea.Feature;
import io.github.saneea.FeatureContext;

public class RandomBytes implements Feature, Feature.CLI, Feature.Out.Bin.Stream {

	private static final String SIZE = "size";
	private static final String SEED = "seed";

	private OutputStream out;

	private CommandLine commandLine;

	@Override
	public String getShortDescription() {
		return "generates random binary data";
	}

	@Override
	public void run(FeatureContext context) throws IOException {

		long count = Long.parseLong(commandLine.getOptionValue(SIZE));

		String seed = commandLine.getOptionValue(SEED);

		Random r = seed == null //
				? new Random()
				: new Random(Long.parseLong(seed));

		byte[] bytes = new byte[1];

		for (int i = 0; i < count; ++i) {
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
						.builder("s")//
						.longOpt(SIZE)//
						.hasArg(true)//
						.argName("bytes count")//
						.required(true)//
						.desc("size of generated data in bytes")//
						.build(), //

				Option//
						.builder()//
						.longOpt(SEED)//
						.hasArg(true)//
						.argName("seed number")//
						.required(false)//
						.desc("seed for random generator")//
						.build()//
		};
	}

}
