package io.github.saneea.textfunction;

import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import io.github.saneea.AppExitException;
import io.github.saneea.Feature.CLI.CommonOptions;

public class Utils {

	public static Map<String, String> toMap(Properties properties) {
		return properties//
				.stringPropertyNames()//
				.stream()//
				.collect(//
						Collectors.toMap(//
								Function.identity(), //
								properties::getProperty));
	}

	public static CommandLine parseCli(String featureName, String[] args, Options cliOptions) {
		return parseCli(featureName, args, cliOptions, new DefaultHelpPrinter(featureName, cliOptions));
	}

	public static CommandLine parseCli(String featureName, String[] args, Options cliOptions, HelpPrinter helpPrinter) {
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

		private final String cmdLineSyntax;
		private final Options options;

		public DefaultHelpPrinter(String cmdLineSyntax, Options options) {
			this.cmdLineSyntax = cmdLineSyntax;
			this.options = options;
		}

		@Override
		public void print(Optional<CommandLine> commandLine) {
			new HelpFormatter().printHelp(cmdLineSyntax, options, true);
		}
	}

}
