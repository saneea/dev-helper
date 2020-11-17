package io.github.saneea.textfunction;

import java.util.Map;
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
		CommandLineParser commandLineParser = new DefaultParser();

		CommandLine commandLine;

		try {
			commandLine = commandLineParser.parse(cliOptions, args);
		} catch (ParseException e) {
			System.err.println(e.getLocalizedMessage());
			printHelp(featureName, cliOptions);
			throw new AppExitException(AppExitException.ExitCode.ERROR, e);
		}

		if (commandLine.hasOption(CommonOptions.HELP)) {
			printHelp(featureName, cliOptions);
			throw new AppExitException(AppExitException.ExitCode.OK);
		}

		return commandLine;
	}

	private static void printHelp(String featureName, Options cliOptions) {
		new HelpFormatter().printHelp(featureName, cliOptions);
	}
}
