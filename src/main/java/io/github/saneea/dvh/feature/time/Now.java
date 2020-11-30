package io.github.saneea.dvh.feature.time;

import java.time.ZonedDateTime;
import java.util.Arrays;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

import io.github.saneea.dvh.Feature;
import io.github.saneea.dvh.Feature.Util.IOConsumer;
import io.github.saneea.dvh.FeatureContext;
import io.github.saneea.dvh.feature.time.format.Format;
import io.github.saneea.dvh.feature.time.format.FormatFactory;

public class Now implements//
		Feature, //
		Feature.CLI, //
		Feature.CLI.Options, //
		Feature.Out.Text.String {

	private static final String FORMAT = "format";
	private static final String FORMAT_SHORT = "f";

	private IOConsumer<String> out;
	private CommandLine commandLine;
	private final FormatFactory formatFactory = new FormatFactory();

	@Override
	public Meta meta(FeatureContext context) {
		String featuresChain = context.getFeaturesChainString();
		return Meta.from(//
				Meta.Description.from(//
						"print current time", //
						"print current date and/or time in one of format"), //
				Arrays.asList(//
						Meta.Example.from(//
								"seconds from 1970-01-01 (known as Unix-time or Epoch-time)", //
								featuresChain + " -" + FORMAT_SHORT + " unix", //
								"1606339702"), //
						Meta.Example.from(//
								"custom date/time pattern", //
								featuresChain + " -" + FORMAT_SHORT + " yyyy-MM-dd--HH:mm:ss", //
								"2020-11-26--00:36:55")//
				));
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
						.builder(FORMAT_SHORT)//
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
