package io.github.saneea.feature;

import java.util.Arrays;

import org.apache.commons.cli.CommandLine;

import io.github.saneea.Feature;
import io.github.saneea.Feature.Util.IOConsumer;
import io.github.saneea.FeatureContext;

public class SlashReplacer implements//
		Feature, //
		Feature.CLI, //
		Feature.In.Text.String, //
		Feature.Out.Text.String//
{
	private String in;
	private IOConsumer<String> out;
	private CommandLine commandLine;

	@Override
	public Meta meta(FeatureContext context) {
		return Meta.from("replace slashes (/) or backslashes (\\)");
	}

	@Override
	public void run(FeatureContext context) throws Exception {
		String[] args = commandLine.getArgs();

		if (args.length > 1) {
			throw new IllegalArgumentException(//
					"Only one parameter (replacement string) is expected, but it were " + //
							args.length + ": " + Arrays.asList(args));
		}

		String slash = args.length != 0//
				? args[0]//
				: "/";

		out.accept(in.replaceAll("(\\\\|\\/)+", "\\" + slash));
	}

	@Override
	public void setIn(String in) {
		this.in = in;
	}

	@Override
	public void setOut(IOConsumer<String> out) {
		this.out = out;
	}

	@Override
	public void setCommandLine(CommandLine commandLine) {
		this.commandLine = commandLine;
	}

}
