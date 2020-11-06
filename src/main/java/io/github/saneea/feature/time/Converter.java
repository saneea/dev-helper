package io.github.saneea.feature.time;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

import io.github.saneea.Feature;
import io.github.saneea.Feature.Util.IOConsumer;
import io.github.saneea.FeatureContext;

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

	@Override
	public String getShortDescription() {
		return "convert time from original format to another one";
	}

	@Override
	public void run(FeatureContext context) throws Exception {
		String inFormat = commandLine.getOptionValue(IN_FORMAT, FORMAT_HUMAN);
		String outFormat = commandLine.getOptionValue(OUT_FORMAT, FORMAT_HUMAN);

		ZonedDateTime zoned2 = parseFormattedTime(in, inFormat);

		String convertedFormatted = getFormattedTime(zoned2, outFormat);

		out.accept(convertedFormatted);
	}

	private static ZonedDateTime parseFormattedTime(String time, String format) {
		switch (format) {
		case FORMAT_JAVA:
			return ZonedDateTime.ofInstant(Instant.ofEpochMilli(Long.valueOf(time)), ZoneId.systemDefault());

		case FORMAT_UNIX:
			return ZonedDateTime.ofInstant(Instant.ofEpochSecond(Long.valueOf(time)), ZoneId.systemDefault());

		case FORMAT_HUMAN:
			return ZonedDateTime.from(DateTimeFormatter.RFC_1123_DATE_TIME.parse(time));

		case FORMAT_ISO:
			return ZonedDateTime.from(DateTimeFormatter.ISO_OFFSET_DATE_TIME.parse(time));

		default:
			return ZonedDateTime.from(DateTimeFormatter.ofPattern(format).parse(time));
		}
	}

	private static String getFormattedTime(ZonedDateTime time, String format) {

		switch (format) {
		case FORMAT_JAVA:
			return String.valueOf(time.toInstant().toEpochMilli());

		case FORMAT_UNIX:
			return String.valueOf(time.toInstant().getEpochSecond());

		case FORMAT_HUMAN:
			return DateTimeFormatter.RFC_1123_DATE_TIME.format(time);

		case FORMAT_ISO:
			return DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(time);

		default:
			return DateTimeFormatter.ofPattern(format).format(time);
		}
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
						.required(false)//
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
						.required(false)//
						.desc("output time format")//
						.build()//
		};
		return options;
	}

}
