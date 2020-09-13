package io.github.saneea.feature;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import io.github.saneea.Feature;
import io.github.saneea.FeatureContext;

public class ToHex implements Feature {

	@Override
	public String getShortDescription() {
		return "convert input binary sequence to hex";
	}

	@Override
	public void run(FeatureContext context) throws IOException {
		run(context.getIn(), context.getOut());
	}

	public void run(InputStream input, OutputStream output) throws IOException {
		try (Writer writer = new OutputStreamWriter(output)) {

			int charCode;
			while ((charCode = input.read()) != -1) {

				String hexString = Integer.toHexString(charCode);
				if (hexString.length() < 2) {
					writer.write('0');
				}
				writer.write(hexString);
			}
		}

	}

}
