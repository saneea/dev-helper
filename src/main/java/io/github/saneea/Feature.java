package io.github.saneea;

import java.io.InputStream;
import java.io.OutputStream;

public interface Feature {

	void run(InputStream input, OutputStream output, String[] args) throws Exception;

}
