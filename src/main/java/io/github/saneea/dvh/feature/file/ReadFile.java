package io.github.saneea.dvh.feature.file;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import io.github.saneea.dvh.Feature;

public class ReadFile//
		extends FileFeature//
		implements Feature.Out.Bin.Stream {

	private OutputStream out;

	@Override
	protected String getDescription() {
		return "transfer bytes from file to standard output";
	}

	@Override
	protected void handleFile(String fileName) throws IOException {
		try (InputStream in = new BufferedInputStream(//
				new FileInputStream(fileName))) {
			in.transferTo(out);
		}
	}

	@Override
	public void setOut(OutputStream out) {
		this.out = out;
	}

}
