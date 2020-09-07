package io.github.saneea;

import java.io.InputStream;
import java.io.OutputStream;

public interface Feature {

	default void run(InputStream in, OutputStream out, OutputStream err, String[] args) throws Exception {
		run(in, out, args);
	}

	void run(InputStream input, OutputStream output, String[] args) throws Exception;

}
