package io.github.saneea.feature.time;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

import io.github.saneea.Feature;
import io.github.saneea.Feature.Util.IOConsumer;
import io.github.saneea.FeatureContext;

public class Now implements//
		Feature, //
		Feature.CLI, //
		Feature.Out.Text.String {

	private static final int MILLIS_IN_SECOND = 1000;
	private static final String FORMAT = "format";
	private static final String FORMAT_UNIX = "unix";
	private static final String FORMAT_JAVA = "java";
	private static final String FORMAT_HUMAN = "human";
	private static final String FORMAT_ISO = "ISO";

	private IOConsumer<String> out;
	private CommandLine commandLine;

	@Override
	public String getShortDescription() {
		return "print current time";
	}

	@Override
	public void run(FeatureContext context) throws Exception {
		String format = commandLine.getOptionValue(FORMAT, FORMAT_HUMAN);
		out.accept(getFormattedTime(format));
	}

	private static String getFormattedTime(String format) {
		switch (format) {
		case FORMAT_JAVA:
			return String.valueOf(System.currentTimeMillis());

		case FORMAT_UNIX:
			return String.valueOf(System.currentTimeMillis() / MILLIS_IN_SECOND);

		case FORMAT_HUMAN:
			return DateTimeFormatter.RFC_1123_DATE_TIME.format(ZonedDateTime.now());

		case FORMAT_ISO:
			return DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(ZonedDateTime.now());

		default:
			return DateTimeFormatter.ofPattern(format).format(ZonedDateTime.now());
		}
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
						.argName(FORMAT_UNIX + //
								'|' + FORMAT_JAVA//
								+ '|' + FORMAT_HUMAN//
								+ '|' + FORMAT_ISO//
								+ "|<pattern>")//
						.required(false)//
						.desc("output time format")//
						.build()//
		};
		return options;
	}

}
