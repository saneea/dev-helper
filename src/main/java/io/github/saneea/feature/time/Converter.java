package io.github.saneea.feature.time;

import java.time.ZonedDateTime;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

import io.github.saneea.Feature;
import io.github.saneea.Feature.Util.IOConsumer;
import io.github.saneea.FeatureContext;
import io.github.saneea.feature.time.format.Format;
import io.github.saneea.feature.time.format.FormatFactory;

public class Converter implements//
		Feature, //
		Feature.CLI, //
		Feature.In.Text.String, //
		Feature.Out.Text.String {

	private static final String IN_FORMAT = "inputFormat";
	private static final String OUT_FORMAT = "outputFormat";

	private String in;
	private IOConsumer<String> out;
	private CommandLine commandLine;

	private final FormatFactory formatFactory = new FormatFactory();

	@Override
	public String getShortDescription() {
		return "convert time from original format to another one";
	}

	@Override
	public void run(FeatureContext context) throws Exception {
		Format inFormat = getFormatFromCLI(IN_FORMAT);
		Format outFormat = getFormatFromCLI(OUT_FORMAT);

		ZonedDateTime timeAsZoned = inFormat.parse(in);
		String converted = outFormat.render(timeAsZoned);

		out.accept(converted);
	}

	private Format getFormatFromCLI(String cliOptionName) {
		return formatFactory.createFormat(commandLine.getOptionValue(cliOptionName));
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
				createFormatCliOption("if", IN_FORMAT, "input time format"), //
				createFormatCliOption("of", OUT_FORMAT, "output time format")//
		};
		return options;
	}

	private Option createFormatCliOption(String shortName, String longName, String description) {
		return Option//
				.builder(shortName)//
				.longOpt(longName)//
				.hasArg(true)//
				.argName(formatFactory.getAvailableFormatsString())//
				.required(true)//
				.desc(description)//
				.build();
	}

}
