package io.github.saneea.textfunction;

import java.util.Collection;
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

import io.github.saneea.AppExitException;
import io.github.saneea.Feature.CLI.CommonOptions;
import io.github.saneea.FeatureContext;

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

	public static CommandLine parseCli(String[] args, Options cliOptions, FeatureContext context) {
		return parseCli(args, cliOptions, new DefaultHelpPrinter(cliOptions, context));
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
		protected final FeatureContext context;
		protected final String cmdLineSyntax;

		public DefaultHelpPrinter(Options options, FeatureContext context) {
			this.options = options;
			this.context = context;
			this.cmdLineSyntax = cmdLineSyntax();
		}

		@Override
		public void print(Optional<CommandLine> commandLine) {
			new HelpFormatter().printHelp(cmdLineSyntax, options, true);
		}

		private String cmdLineSyntax() {
			return context//
					.getFeaturesChain()//
					.collect(Collectors.joining(" "));
		}
	}

}
