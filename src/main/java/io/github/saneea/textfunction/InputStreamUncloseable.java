package io.github.saneea.textfunction;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

public class InputStreamUncloseable extends FilterInputStream {

	public InputStreamUncloseable(InputStream in) {
		super(in);
	}

	@Override
	public void close() throws IOException {
		// just skip
	}

}
