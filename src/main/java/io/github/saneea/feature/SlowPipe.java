package io.github.saneea.feature;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

import io.github.saneea.Feature;
import io.github.saneea.FeatureContext;

public class SlowPipe implements Feature, Feature.CLI, Feature.In.Bin.Stream, Feature.Out.Bin.Stream {

	private static String DELAY = "delay";

	private InputStream in;
	private OutputStream out;
	private CommandLine commandLine;

	@Override
	public String getShortDescription() {
		return "transfer bytes with delay";
	}

	@Override
	public void run(FeatureContext context) throws IOException, InterruptedException {

		long delay = Long.parseLong(commandLine.getOptionValue(DELAY));

		int byteCode;
		while ((byteCode = in.read()) != -1) {

			Thread.sleep(delay);

			out.write(byteCode);
		}
	}

	@Override
	public void setIn(InputStream in) {
		this.in = in;
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
		return new Option[] { Option//
				.builder("d")//
				.longOpt(DELAY)//
				.hasArg(true)//
				.argName("millis")//
				.required(true)//
				.desc("delay before each byte")//
				.build() };
	}

}
