package io.github.saneea.dvh.utils;

import java.io.IOException;
import java.io.PushbackReader;
import java.io.Reader;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import io.github.saneea.dvh.AppExitException;
import io.github.saneea.dvh.Feature;
import io.github.saneea.dvh.Feature.CLI.CommonOptions;
import io.github.saneea.dvh.Feature.Meta.Example;
import io.github.saneea.dvh.FeatureContext;

public class Utils {

	public static final String[] NO_ARGS = {};

	public static Map<String, String> toMap(Properties properties) {
		return properties//
				.stringPropertyNames()//
				.stream()//
				.collect(//
						Collectors.toMap(//
								Function.identity(), //
								properties::getProperty));
	}

	public static int getMaxStringLength(Collection<String> strings) {
		return getMaxStringLength(strings.stream());
	}

	public static int getMaxStringLength(Stream<String> strings) {
		return strings//
				.mapToInt(String::length)//
				.max()//
				.orElse(0);
	}

	public static String repeatString(String s, int level) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < level; ++i) {
			sb.append(s);
		}
		return sb.toString();
	}

	public static Reader skipBom(Reader originalReader) throws IOException {
		PushbackReader ret = new PushbackReader(originalReader);

		int firstChar = ret.read();

		if (firstChar != Const.BOM_CHAR && //
				firstChar != Const.END_OF_STREAM) {
			ret.unread(firstChar);
		}

		return ret;
	}

	public static CommandLine parseCli(String[] args, Options cliOptions, Feature feature, FeatureContext context) {
		return parseCli(args, cliOptions, new DefaultHelpPrinter(cliOptions, feature, context));
	}

	public static CommandLine parseCli(String[] args, Options cliOptions, HelpPrinter helpPrinter) {
		CommandLineParser commandLineParser = new DefaultParser();

		CommandLine commandLine;

		try {
			commandLine = commandLineParser.parse(cliOptions, args);
		} catch (ParseException e) {
			System.err.println(e.getLocalizedMessage());
			helpPrinter.print(Optional.empty());
			throw new AppExitException(AppExitException.ExitCode.ERROR, e);
		}

		if (commandLine.hasOption(CommonOptions.HELP)) {
			helpPrinter.print(Optional.of(commandLine));
			throw new AppExitException(AppExitException.ExitCode.OK);
		}

		return commandLine;
	}

	public interface HelpPrinter {
		void print(Optional<CommandLine> commandLine);
	}

	public static class DefaultHelpPrinter implements HelpPrinter {
		protected final Options options;
		protected final Feature feature;
		protected final FeatureContext context;
		protected final String cmdLineSyntax;

		public DefaultHelpPrinter(Options options, Feature feature, FeatureContext context) {
			this.options = options;
			this.feature = feature;
			this.context = context;
			this.cmdLineSyntax = context.getFeaturesChainString();
		}

		@Override
		public void print(Optional<CommandLine> commandLine) {
			printDescription();
			printCLI();
			printExamples();
		}

		protected void printExamples() {
			List<Example> examples = feature.meta(context).examples();
			int examplesCount = examples.size();

			if (examplesCount > 0) {
				System.out.println();
				System.out.println("Examples (" + examplesCount + "):");

				for (int i = 0; i < examplesCount; ++i) {
					Example example = examples.get(i);
					System.out.println("" + (i + 1) + ". " + example.name());
					System.out.println("run command:");
					System.out.println("\t" + example.body());

					example.result().ifPresent(result -> {
						System.out.println("output:");
						System.out.println("\t" + result);
					});

					System.out.println();
				}
			}
		}

		protected void printCLI() {
			new HelpFormatter().printHelp(cmdLineSyntax, options, true);
		}

		protected void printDescription() {
			System.out.println(feature.meta(context).description().detailed());
			System.out.println();
		}
	}

}
