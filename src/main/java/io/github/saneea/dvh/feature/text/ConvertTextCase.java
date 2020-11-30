package io.github.saneea.dvh.feature.text;

import java.io.Reader;
import java.io.Writer;

import org.apache.commons.cli.CommandLine;

import io.github.saneea.dvh.Feature;
import io.github.saneea.dvh.FeatureContext;

public abstract class ConvertTextCase implements Feature, Feature.CLI, Feature.In.Text.Reader, Feature.Out.Text.Writer {

	public static class Upper extends ConvertTextCase {
		public Upper() {
			super(Character::toUpperCase, Character::toUpperCase);
		}

		@Override
		public Meta meta(FeatureContext context) {
			return Meta.from("convert text to upper case (aBcDe -> ABCDE)");
		}
	}

	public static class Lower extends ConvertTextCase {
		public Lower() {
			super(Character::toLowerCase, Character::toLowerCase);
		}

		@Override
		public Meta meta(FeatureContext context) {
			return Meta.from("convert text to lower case (aBcDe -> abcde)");
		}
	}

	private final CharConverter charConverter;
	private final IntConverter intConverter;

	private Reader in;
	private Writer out;
	private CommandLine commandLine;

	public ConvertTextCase(CharConverter charConverter, IntConverter intConverter) {
		this.charConverter = charConverter;
		this.intConverter = intConverter;
	}

	@Override
	public void run(FeatureContext context) throws Exception {
		if (commandLine.hasOption(CommonOptions.NON_BUFFERED_STREAMS)) {
			int codePoint;
			while ((codePoint = in.read()) != -1) {
				out.write(intConverter.convert(codePoint));
				out.flush();
			}
		} else {
			char[] buf = new char[4096];
			int wasRead;
			while ((wasRead = in.read(buf)) != -1) {
				convertChars(buf, wasRead);
				out.write(buf, 0, wasRead);
			}
		}
	}

	private void convertChars(char[] buf, int size) {
		for (int i = 0; i < size; ++i) {
			buf[i] = convertChar(buf[i]);
		}
	}

	private char convertChar(char c) {
		return charConverter.convert(c);
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

	@FunctionalInterface
	private interface CharConverter {
		char convert(char c);
	}

	@FunctionalInterface
	private interface IntConverter {
		int convert(int i);
	}

}
