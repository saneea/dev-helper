package io.github.saneea.feature.text;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

import io.github.saneea.Feature;
import io.github.saneea.FeatureContext;

public class AddNewLine implements//
		Feature, //
		Feature.In.Text.Reader, //
		Feature.Out.Text.Writer {

	private Reader in;
	private Writer out;

	@Override
	public Meta meta(FeatureContext context) {
		return Meta.from("add platform-dependent 'newline' sequence (e.g. '\\n', '\\r\\n') at the end");
	}

	@Override
	public void run(FeatureContext context) throws IOException {
		in.transferTo(out);
		out.append(System.lineSeparator());
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
