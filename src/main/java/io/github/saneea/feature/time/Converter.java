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

	private static final String FORMAT_UNIX = "unix";
	private static final String FORMAT_JAVA = "java";
	private static final String FORMAT_HUMAN = "human";
	private static final String FORMAT_ISO = "ISO";

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
				Option//
						.builder("if")//
						.longOpt(IN_FORMAT)//
						.hasArg(true)//
						.argName(FORMAT_UNIX + //
								'|' + FORMAT_JAVA//
								+ '|' + FORMAT_HUMAN//
								+ '|' + FORMAT_ISO//
								+ "|<pattern>")//
						.required(true)//
						.desc("input time format")//
						.build(), //

				Option//
						.builder("of")//
						.longOpt(OUT_FORMAT)//
						.hasArg(true)//
						.argName(FORMAT_UNIX + //
								'|' + FORMAT_JAVA//
								+ '|' + FORMAT_HUMAN//
								+ '|' + FORMAT_ISO//
								+ "|<pattern>")//
						.required(true)//
						.desc("output time format")//
						.build()//
		};
		return options;
	}

}
