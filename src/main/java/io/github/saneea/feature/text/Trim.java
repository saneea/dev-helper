package io.github.saneea.feature.text;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.LinkedList;
import java.util.Queue;
import java.util.function.Supplier;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

import io.github.saneea.Feature;
import io.github.saneea.FeatureContext;

public class Trim implements//
		Feature, //
		Feature.CLI, //
		Feature.In.Text.Reader, //
		Feature.Out.Text.Writer {

	private static final String LEFT_TRIM = "leftTrim";
	private static final String RIGHT_TRIM = "rightTrim";

	private Reader in;
	private Writer out;
	private CommandLine commandLine;

	@Override
	public String getShortDescription() {
		return "trim leading and/or trailing whitespaces";
	}

	@Override
	public void run(FeatureContext context) throws IOException {

		TextConverter converter = TextConverter.orig();

		converter = addTrimmer(LEFT_TRIM, converter, LeftTrimmer::new);
		converter = addTrimmer(RIGHT_TRIM, converter, RightTrimmer::new);

		int charCode;
		while ((charCode = in.read()) != -1) {
			converter.convertChar(charCode, out::write);
		}
	}

	private TextConverter addTrimmer(//
			String optName, //
			TextConverter currentConverter, //
			Supplier<TextConverter> newConverterCtor) {

		String optValue = commandLine//
				.getOptionValue(optName, "true")//
				.toLowerCase();

		switch (optValue) {
		case "true":
			return currentConverter.andThen(newConverterCtor.get());
		case "false":
			return currentConverter;
		default:
			throw new IllegalArgumentException("Invalid " + optName + " value:" + optValue);
		}
	}

	@Override
	public void setIn(Reader in) {
		this.in = in;
	}

	@Override
	public void setOut(Writer out) {
		this.out = out;
	}

	@Override
	public void setCommandLine(CommandLine commandLine) {
		this.commandLine = commandLine;
	}

	@Override
	public Option[] getOptions() {
		Option[] options = { //
				createTrimCliOption("l", LEFT_TRIM, "trim leading whitespaces"), //
				createTrimCliOption("r", RIGHT_TRIM, "trim trailing whitespaces")//
		};
		return options;
	}

	private Option createTrimCliOption(String shortName, String longName, String description) {
		return Option//
				.builder(shortName)//
				.longOpt(longName)//
				.hasArg(true)//
				.argName("true|false")//
				.required(false)//
				.desc(description + " (default is 'true')")//
				.build();
	}

	private static class LeftTrimmer implements TextConverter {
		boolean trimming = true;

		@Override
		public void convertChar(int charCode, CharWriter w) throws IOException {
			if (!trimming) {
				w.write(charCode);
			} else if (!Character.isWhitespace(charCode)) {
				w.write(charCode);
				trimming = false;
			}

		}
	}

	private static class RightTrimmer implements TextConverter {

		private final Queue<Integer> buffer = new LinkedList<>();

		@Override
		public void convertChar(int charCode, CharWriter w) throws IOException {
			if (Character.isWhitespace(charCode)) {
				buffer.offer(charCode);
			} else {
				flush(w);
				w.write(charCode);
			}
		}

		private void flush(CharWriter w) throws IOException {
			Integer fromBuff;
			while ((fromBuff = buffer.poll()) != null) {
				w.write(fromBuff);
			}
		}
	}

	@FunctionalInterface
	private interface CharWriter {
		void write(int charCode) throws IOException;
	}

	@FunctionalInterface
	private interface TextConverter {

		void convertChar(int charCode, CharWriter w) throws IOException;

		default TextConverter andThen(TextConverter nextConverter) {
			return (charCode, w) -> //
			convertChar(charCode, charCodeConverted -> //
			nextConverter.convertChar(charCodeConverted, w));
		}

		static TextConverter orig() {
			return (charCode, w) -> w.write(charCode);
		}

	}

}
