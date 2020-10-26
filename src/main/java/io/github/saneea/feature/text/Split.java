package io.github.saneea.feature.text;

import java.io.IOException;
import java.io.PrintStream;
import java.io.Reader;

import io.github.saneea.Feature;
import io.github.saneea.FeatureContext;

public class Split implements//
		Feature, //
		Feature.In.Text.Reader, //
		Feature.Out.Text.PrintStream {

	private static final int LINE_SIZE = 60;// TODO set it as CLI param

	private Reader in;
	private PrintStream out;

	@Override
	public String getShortDescription() {
		return "split text as lines";
	}

	@Override
	public void run(FeatureContext context) throws Exception {
		while (transferLine()) {
			// no code
		}
	}

	private boolean transferLine() throws IOException {
		for (int i = 0; i < LINE_SIZE; ++i) {
			int charCode = in.read();

			if (charCode == -1) {
				if (i != 0) {
					out.println();
				}
				return false;
			}

			out.print((char) charCode);
		}
		out.println();

		return true;
	}

	@Override
	public void setIn(Reader in) {
		this.in = in;
	}

	@Override
	public void setOut(PrintStream out) {
		this.out = out;
	}
}
