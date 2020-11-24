package io.github.saneea.feature.binary;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;

import io.github.saneea.Feature;
import io.github.saneea.FeatureContext;

public class ToHex implements Feature, Feature.In.Bin.Stream, Feature.Out.Text.PrintStream {

	private InputStream in;
	private PrintStream out;

	@Override
	public Meta meta(FeatureContext context) {
		return Meta.from("convert input binary sequence to hex");
	}

	@Override
	public void run(FeatureContext context) throws IOException {
		run(in, out);
	}

	public static void run(InputStream input, PrintStream out) throws IOException {
		int charCode;
		while ((charCode = input.read()) != -1) {

			String hexString = Integer.toHexString(charCode);
			if (hexString.length() < 2) {
				out.print('0');
			}
			out.print(hexString);
		}
	}

	@Override
	public void setIn(InputStream in) {
		this.in = in;
	}

	@Override
	public void setOut(PrintStream out) {
		this.out = out;
	}

}
