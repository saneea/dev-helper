package io.github.saneea.feature.binary;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

import io.github.saneea.Feature;
import io.github.saneea.FeatureContext;

public class Hash implements Feature, Feature.CLI, Feature.In.Bin.Stream, Feature.Out.Text.PrintStream {

	private static int BUFFER_SIZE = 4096;

	private CommandLine commandLine;

	private InputStream in;
	private PrintStream out;

	@Override
	public String getShortDescription() {
		return "calc hash (md5, sha-* etc.)";
	}

	private static void execute(InputStream input, PrintStream out, String alg)
			throws NoSuchAlgorithmException, IOException {

		MessageDigest md = MessageDigest.getInstance(alg);

		byte[] buf = new byte[BUFFER_SIZE];

		int len;
		while ((len = input.read(buf)) != -1) {
			md.update(buf, 0, len);
		}

		ToHex.run(new ByteArrayInputStream(md.digest()), out);
	}

	@Override
	public void run(FeatureContext context) throws NoSuchAlgorithmException, IOException {
		String alg = commandLine.getOptionValue(Params.ALGORITHM);
		execute(in, out, alg);
	}

	@Override
	public Option[] getOptions() {
		return Params.createOptions();
	}

	@Override
	public void setCommandLine(CommandLine commandLine) {
		this.commandLine = commandLine;
	}

	public static class Params {

		public static String ALGORITHM = "algorithm";

		private static Option[] createOptions() {

			Option[] options = { //
					Option//
							.builder("a")//
							.longOpt(ALGORITHM)//
							.hasArg(true)//
							.argName("algorithm name")//
							.required(true)//
							.desc("hash algorithm (e.g. md5)")//
							.build() };

			return options;
		}
	}

	@Override
	public void setOut(PrintStream out) {
		this.out = out;
	}

	@Override
	public void setIn(InputStream in) {
		this.in = in;
	}
}