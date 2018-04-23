package io.github.saneea.feature;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;

import io.github.saneea.Feature;

public class ToFile implements Feature {

	@Override
	public void run(InputStream input, OutputStream output, String[] args) throws Exception {
		try {
			runInternal(input, output, args);
		} catch (Exception e) {
			try (PrintWriter printWriter = new PrintWriter("I am exception.txt")) {
				e.printStackTrace(printWriter);
			}
		}
	}

	public void runInternal(InputStream input, OutputStream output, String[] args) throws Exception {
		File tmpOutFile = File.createTempFile("XmlPrettyPrint", "xml", new File("."));
		// tmpOutFile.deleteOnExit();

		try (OutputStream tmpFileStream = new BufferedOutputStream(new FileOutputStream(tmpOutFile))) {
			transfer(input, tmpFileStream);
		}

		try (InputStream tmpFileStream = new BufferedInputStream(new FileInputStream(tmpOutFile))) {
			transfer(tmpFileStream, output);
		}
	}

	private void transfer(InputStream input, OutputStream output) throws IOException {
		byte[] buf = new byte[4096];
		int wasRead;
		while ((wasRead = input.read(buf)) != -1) {
			output.write(buf, 0, wasRead);
		}
	}

}
