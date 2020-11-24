package io.github.saneea.feature.time;

import java.time.ZonedDateTime;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

import io.github.saneea.Feature;
import io.github.saneea.Feature.Util.IOConsumer;
import io.github.saneea.FeatureContext;
import io.github.saneea.feature.time.format.Format;
import io.github.saneea.feature.time.format.FormatFactory;

public class Now implements//
		Feature, //
		Feature.CLI, //
		Feature.CLI.Options, //
		Feature.Out.Text.String {

	private static final String FORMAT = "format";

	private IOConsumer<String> out;
	private CommandLine commandLine;
	private final FormatFactory formatFactory = new FormatFactory();

	@Override
	public Meta meta(FeatureContext context) {
		return Meta.from("print current time");
	}

	@Override
	public void run(FeatureContext context) throws Exception {
		String formatNameOrPattern = commandLine.getOptionValue(FORMAT, FormatFactory.FORMAT_HUMAN);
		Format format = formatFactory.createFormat(formatNameOrPattern);
		String formattedTime = format.render(ZonedDateTime.now());
		out.accept(formattedTime);
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
						.builder("f")//
						.longOpt(FORMAT)//
						.hasArg(true)//
						.argName(formatFactory.getAvailableFormatsString())//
						.required(false)//
						.desc("output time format")//
						.build()//
		};
		return options;
	}

}
