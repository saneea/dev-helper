package io.github.saneea.dvh.feature.text;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

import io.github.saneea.dvh.Feature;
import io.github.saneea.dvh.FeatureContext;

public class Pipe implements//
		Feature, //
		Feature.In.Text.Reader, //
		Feature.Out.Text.Writer {

	private Reader in;
	private Writer out;

	@Override
	public Meta meta(FeatureContext context) {
		return Meta.from("just transfer text from std_in to std_out");
	}

	@Override
	public void run(FeatureContext context) throws IOException {
		in.transferTo(out);
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
