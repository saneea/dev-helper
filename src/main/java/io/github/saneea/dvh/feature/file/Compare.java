package io.github.saneea.dvh.feature.file;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import io.github.saneea.dvh.Feature;
import io.github.saneea.dvh.Feature.Util.IOConsumer;

public class Compare//
		extends FileFeature//
		implements//
		Feature.In.Bin.Stream, //
		Feature.Out.Text.String {

	private InputStream in;
	private IOConsumer<String> out;

	@Override
	protected String getDescription() {
		return "compare file with standard input (full binary comparison)";
	}

	@Override
	protected void handleFile(String fileName) throws IOException {
		try (InputStream fileIn = new BufferedInputStream(//
				new FileInputStream(fileName))) {

			out.accept(//
					compareStreams(fileIn, in)//
							? "=="//
							: "!=");
		}
	}

	private boolean compareStreams(InputStream s1, InputStream s2) throws IOException {
		while (true) {
			int b1 = s1.read();
			int b2 = s2.read();

			if (b1 != b2) {
				return false;
			}

			if (b1 == -1) {
				return true;
			}
		}
	}

	@Override
	public void setIn(InputStream in) {
		this.in = in;
	}

	@Override
	public void setOut(IOConsumer<String> out) {
		this.out = out;
	}

}
