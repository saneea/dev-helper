package io.github.saneea.feature.text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.io.Writer;

import io.github.saneea.Feature;
import io.github.saneea.FeatureContext;

public class JoinLines implements//
		Feature, //
		Feature.In.Text.Reader, //
		Feature.Out.Text.Writer {

	private Reader in;
	private Writer out;

	@Override
	public Meta meta(FeatureContext context) {
		return Meta.from("join lines to one line");
	}

	@Override
	public void run(FeatureContext context) throws Exception {

		try (BufferedReader bufferedIn = new BufferedReader(in)) {
			bufferedIn//
					.lines()//
					.map(String::trim)//
					.forEach(this::writeLine);
		}
	}

	public void writeLine(String s) {
		try {
			out.write(s);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
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
