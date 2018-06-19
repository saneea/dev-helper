package io.github.saneea.feature;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;

import io.github.saneea.Feature;

public class PrintArgsFeature implements Feature {

	@Override
	public void run(InputStream input, OutputStream output, String[] args) throws Exception {
		try (PrintWriter writer = new PrintWriter(output)) {
			for (String arg : args) {
				writer.println(arg);
			}
		}
	}

}
