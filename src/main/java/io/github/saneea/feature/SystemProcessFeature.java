package io.github.saneea.feature;

import java.io.InputStream;
import java.io.OutputStream;

import io.github.saneea.Feature;

public class SystemProcessFeature implements Feature {

	@Override
	public void run(InputStream input, OutputStream output, String[] args) throws Exception {
		// TODO Auto-generated method stub

		ProcessBuilder processBuilder = new ProcessBuilder("calc.exe");
		Process process = processBuilder.start();

		System.out.println(process.waitFor());

		// new
	}

}
