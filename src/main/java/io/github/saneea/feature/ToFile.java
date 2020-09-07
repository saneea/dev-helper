package io.github.saneea.feature;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import io.github.saneea.Feature;

public class ToFile implements Feature {

	@Override
	public void run(InputStream input, OutputStream output, OutputStream err, String[] args) throws Exception {

		String outFileName = args[0];
		String commandLine = args[1];

		Process forkProc = Runtime.getRuntime().exec(commandLine);

		try (ByteArrayOutputStream buffer = new ByteArrayOutputStream(); //
				InputStream forkProcOut = new BufferedInputStream(forkProc.getInputStream())) {

			forkProcOut.transferTo(buffer);

			int exitCode = forkProc.waitFor();

			if (exitCode == 0) {
				try (OutputStream outFileStream = new BufferedOutputStream(new FileOutputStream(outFileName))) {
					buffer.writeTo(outFileStream);
				}
			} else {
				try (InputStream forkProcErr = new BufferedInputStream(forkProc.getErrorStream())) {
					forkProcErr.transferTo(err);
				}
				System.exit(exitCode);
			}
		}
	}

	@Override
	public void run(InputStream input, OutputStream output, String[] args) throws Exception {
		throw new IllegalStateException(
				"This method should not be used. Use run(InputStream, OutputStream, OutputStream, String[])");
	}

}
