package io.github.saneea.textfunction;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Base64;

import io.github.saneea.Feature;

public class ToBase64 implements Feature {

	@Override
	public void run(InputStream input, OutputStream output, String[] args) throws IOException {
		try (OutputStream base64stream = Base64.getEncoder().wrap(output)) {
			input.transferTo(base64stream);
		}
	}

}
