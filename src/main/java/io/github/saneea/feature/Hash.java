package io.github.saneea.feature;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import io.github.saneea.Feature;

public class Hash implements Feature {

	private static int BUFFER_SIZE = 4096;

	public static void execute(InputStream input, OutputStream output, String alg)
			throws NoSuchAlgorithmException, IOException {

		MessageDigest md = MessageDigest.getInstance(alg);

		byte[] buf = new byte[BUFFER_SIZE];

		int len;
		while ((len = input.read(buf)) != -1) {
			md.update(buf, 0, len);
		}

		new ToHex().run(new ByteArrayInputStream(md.digest()), output, new String[] {});
	}

	@Override
	public void run(InputStream input, OutputStream output, String[] args) throws Exception {
		Options options = Params.createOptions();

		CommandLineParser commandLineParser = new DefaultParser();
		CommandLine commandLine = commandLineParser.parse(options, args);

		String alg = commandLine.getOptionValue(Params.ALGORITHM);

		execute(input, output, alg);
	}

	public static class Params {

		public static String ALGORITHM = "algorithm";

		private static Options createOptions() {
			Options options = new Options()//
					.addOption(Option//
							.builder("a")//
							.longOpt(ALGORITHM)//
							.hasArg(true)//
							.argName("algorithm")//
							.required(true)//
							.desc("hash algorithm")//
							.build());

			return options;
		}
	}
}
