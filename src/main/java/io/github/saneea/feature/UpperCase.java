package io.github.saneea.feature;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;

import io.github.saneea.Feature;

public class UpperCase implements Feature {

	@Override
	public void run(InputStream input, OutputStream output, String[] args) throws Exception {
		try (Reader reader = new InputStreamReader(input); //
				Writer writer = new OutputStreamWriter(output)) {
			char[] buf = new char[4096];
			int wasRead;
			while ((wasRead = reader.read(buf)) != -1) {
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

}
