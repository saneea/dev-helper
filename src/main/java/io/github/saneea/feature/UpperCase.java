package io.github.saneea.feature;

import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;

import io.github.saneea.Feature;
import io.github.saneea.FeatureContext;

public class UpperCase implements Feature, Feature.In.Text.Reader {

	private Reader in;

	@Override
	public String getShortDescription() {
		return "convert text to UPPER CASE";
	}

	@Override
	public void run(FeatureContext context) throws Exception {
		try (Writer writer = new OutputStreamWriter(context.getOut())) {
			char[] buf = new char[4096];
			int wasRead;
			while ((wasRead = in.read(buf)) != -1) {
				convertChars(buf, wasRead);
				writer.write(buf, 0, wasRead);
			}
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
	public void setReader(Reader in) {
		this.in = in;
	}

}
