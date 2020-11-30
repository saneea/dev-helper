package io.github.saneea.dvh.feature.text;

import java.util.Arrays;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

import io.github.saneea.dvh.Feature;
import io.github.saneea.dvh.Feature.Util.IOConsumer;
import io.github.saneea.dvh.FeatureContext;

public class SlashReplacer implements//
		Feature, //
		Feature.CLI, //
		Feature.CLI.Options, //
		Feature.In.Text.String, //
		Feature.Out.Text.String//
{
	private static final String NEW_SLASH = "newSlash";
	private static final String NEW_SLASH_SHORT = "n";

	private String in;
	private IOConsumer<String> out;
	private CommandLine commandLine;

	@Override
	public Meta meta(FeatureContext context) {
		String echoPipeFeature = "echo some////path\\\\to\\\\file.txt | " + context.getFeaturesChainString();

		return Meta.from(//
				Meta.Description.from(//
						"replace slashes (/) or backslashes (\\)", //
						"replace slashes (/) or backslashes (\\) to specified string"//
				), //
				Arrays.asList(//
						Meta.Example.from(//
								"replace to slashes", //
								echoPipeFeature, //
								"some/path/to/file.txt"), //
						Meta.Example.from(//
								"replace to custom string", //
								echoPipeFeature + " -" + NEW_SLASH_SHORT + " \\", //
								"some\\path\\to\\file.txt"))//
		);
	}

	@Override
	public void run(FeatureContext context) throws Exception {
		String slash = commandLine.getOptionValue(NEW_SLASH, "/");
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

	@Override
	public Option[] getOptions() {
		Option[] options = { //
				Option//
						.builder(NEW_SLASH_SHORT)//
						.longOpt(NEW_SLASH)//
						.hasArg(true)//
						.argName("slash string")//
						.required(false)//
						.desc("set new slash string")//
						.build() };

		return options;
	}

}
