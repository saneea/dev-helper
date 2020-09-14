package io.github.saneea.feature;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import io.github.saneea.Feature;
import io.github.saneea.FeatureContext;

public class Hash implements Feature {

	private static int BUFFER_SIZE = 4096;

	@Override
	public String getShortDescription() {
		return "calc hash (md5, sha-* etc.)";
	}

	public static void execute(InputStream input, OutputStream output, String alg)
			throws NoSuchAlgorithmException, IOException {

		MessageDigest md = MessageDigest.getInstance(alg);

		byte[] buf = new byte[BUFFER_SIZE];

		int len;
		while ((len = input.read(buf)) != -1) {
			md.update(buf, 0, len);
		}

		new ToHex().run(new ByteArrayInputStream(md.digest()), output);
	}

	@Override
	public void run(FeatureContext context) throws ParseException, NoSuchAlgorithmException, IOException {
		Optional<CommandLine> commandLine = parseArgs(context);

		if (commandLine.isPresent()) {
			String alg = commandLine.get().getOptionValue(Params.ALGORITHM);
			execute(context.getIn(), context.getOut(), alg);
		}
	}

	private Optional<CommandLine> parseArgs(FeatureContext context) {
		Options options = Params.createOptions();
		CommandLineParser commandLineParser = new DefaultParser();

		try {
			return Optional.of(commandLineParser.parse(options, context.getArgs()));
		} catch (ParseException e) {
			context.getErr().println(e.getLocalizedMessage());
			new HelpFormatter().printHelp(context.getFeatureAlias(), options);

			return Optional.empty();
		}
	}

	public static class Params {

		public static String ALGORITHM = "algorithm";

		private static Options createOptions() {

			Options options = new Options()//
					.addOption(Option//
							.builder("a")//
							.longOpt(ALGORITHM)//
							.hasArg(true)//
							.argName("algorithm name")//
							.required(true)//
							.desc("hash algorithm (e.g. md5)")//
							.build());

			return options;
		}
	}
}
