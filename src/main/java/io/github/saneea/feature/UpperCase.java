package io.github.saneea.feature;

import java.io.Reader;
import java.io.Writer;

import io.github.saneea.Feature;
import io.github.saneea.FeatureContext;

public class UpperCase implements Feature, Feature.In.Text.Reader, Feature.Out.Text.Writer {

	private Reader in;
	private Writer out;

	@Override
	public String getShortDescription() {
		return "convert text to UPPER CASE";
	}

	@Override
	public void run(FeatureContext context) throws Exception {
		char[] buf = new char[4096];
		int wasRead;
		while ((wasRead = in.read(buf)) != -1) {
			convertChars(buf, wasRead);
			out.write(buf, 0, wasRead);
		}
	}

	private void convertChars(char[] buf, int size) {
		for (int i = 0; i < size; ++i) {
			buf[i] = convertChar(buf[i]);
		}
	}

	private char convertChar(char c) {
		return Character.toUpperCase(c);
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
