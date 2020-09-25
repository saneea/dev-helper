package io.github.saneea.feature;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;

import io.github.saneea.Feature;
import io.github.saneea.FeatureContext;
import io.github.saneea.api.CLIParameterized;
import io.github.saneea.api.InputStreamInputtable;
import io.github.saneea.api.PrintStreamOutputable;

public class ToHex implements Feature, CLIParameterized, InputStreamInputtable, PrintStreamOutputable {

	private InputStream in;
	private PrintStream out;

	@Override
	public String getShortDescription() {
		return "convert input binary sequence to hex";
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
	public void setInputStream(InputStream in) {
		this.in = in;
	}

	@Override
	public void setPrintStreamOut(PrintStream out) {
		this.out = out;
	}

}
