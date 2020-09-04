package io.github.saneea.feature;

import java.io.InputStream;
import java.io.OutputStream;

import io.github.saneea.Feature;

public class Sleep implements Feature {

	@Override
	public void run(InputStream input, OutputStream output, String[] args) throws Exception {
		Thread.sleep(Long.parseLong(args[0]));
	}

}
