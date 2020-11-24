package io.github.saneea.feature.text;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

import io.github.saneea.Feature;
import io.github.saneea.FeatureContext;

public class ToCharArray implements//
		Feature, //
		Feature.In.Text.Reader, //
		Feature.Out.Text.Writer {

	private Reader in;
	private Writer out;

	@Override
	public Meta meta(FeatureContext context) {
		return Meta.from("convert input string to char array");
	}

	@Override
	public void run(FeatureContext context) throws IOException {
		int charCode;
		boolean first = true;

		while ((charCode = in.read()) != -1) {

			if (first) {
				first = false;
			} else {
				out.append(", ");
			}

			out.append(//
					wrapInQuotes(//
							wrapIfEscaped(//
									(char) charCode)));

		}
	}

	private String wrapInQuotes(String s) {
		return "'" + s + "'";
	}

	private String wrapIfEscaped(char c) {
		switch (c) {
		case '\0':
			return "\\0";
		case '\r':
			return "\\r";
		case '\n':
			return "\\n";
		case '\t':
			return "\\t";
		case '\f':
			return "\\f";
		case '\b':
			return "\\b";

		case '\'':
		case '\\':
			return "\\" + c;

		default:
			return "" + c;
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

}
