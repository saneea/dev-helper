package io.github.saneea.dvh.feature.file;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import io.github.saneea.dvh.Feature;

public class WriteFile//
		extends FileContentTransfer//
		implements Feature.In.Bin.Stream {

	private InputStream in;

	@Override
	protected String getDescription() {
		return "transfer bytes from standard input to file";
	}

	@Override
	protected void handleFile(String fileName) throws IOException {
		try (OutputStream out = new BufferedOutputStream(//
				new FileOutputStream(fileName))) {
			in.transferTo(out);
		}
	}

	@Override
	public void setIn(InputStream in) {
		this.in = in;
	}

}
