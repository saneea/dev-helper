package io.github.saneea.feature;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import io.github.saneea.Feature;

public class UUID implements Feature {

	@Override
	public void run(InputStream input, OutputStream output, String[] args) throws Exception {
		try (Writer writer = new OutputStreamWriter(output)) {
			writer.write(java.util.UUID.randomUUID().toString());
		}
	}

}
