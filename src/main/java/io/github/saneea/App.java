package io.github.saneea;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import io.github.saneea.feature.Gui;
import io.github.saneea.feature.PrintArgsFeature;
import io.github.saneea.feature.SystemProcessFeature;
import io.github.saneea.feature.ToFile;
import io.github.saneea.feature.UpperCase;
import io.github.saneea.feature.XmlPrettyPrint;
import io.github.saneea.feature.XmlToLine;

public class App {

	enum FeatureAlias {
		GUI("gui", Gui.class), //
		XML_TO_LINE("xmlToLine", XmlToLine.class), //
		XML_PRETTY_PRINT("xmlPrettyPrint", XmlPrettyPrint.class), //
		UPPER_CASE("upperCase", UpperCase.class), //
		TO_FILE("toFile", ToFile.class), //
		SYSTEM_PROCESS_FEATURE("systemProcess", SystemProcessFeature.class), //
		PRINT_ARGS_FEATURE("printArgs", PrintArgsFeature.class);

		public final String asString;

		public final Class<? extends Feature> featureClass;

		FeatureAlias(String asString, Class<? extends Feature> featureClass) {
			this.asString = asString;
			this.featureClass = featureClass;
		}
	}

	public static void main(String[] args) throws Exception {
		Options options = createOptions();

		CommandLineParser commandLineParser = new DefaultParser();
		CommandLine commandLine;
		try {
			commandLine = commandLineParser.parse(options, args);
		} catch (ParseException e) {
			System.out.println(e.getMessage());
			printHelp(options);
			System.exit(1);
			commandLine = null;
		}

		if (commandLine.hasOption("h")) {
			printHelp(options);
		}

		String featureName = commandLine.getOptionValue("feature", "gui");

		Class<? extends Feature> featureClass = getFeatureClass(featureName);

		Feature feature = featureClass.newInstance();

		try (InputStream input = new BufferedInputStream(System.in); //
				OutputStream output = new BufferedOutputStream(System.out)) {
			feature.run(input, output, args);
		}

	}

	private static Class<? extends Feature> getFeatureClass(String featureName) {
		for (FeatureAlias feature : FeatureAlias.values()) {
			if (feature.asString.equals(featureName)) {
				return feature.featureClass;
			}
		}
		throw new IllegalArgumentException("Unknown feature: \"" + featureName + "\"");
	}

	private static Options createOptions() {
		Options options = new Options()//
				.addOption(Option//
						.builder("h")//
						.longOpt("help")//
						.required(false)//
						.desc("print this help")//
						.build())//
				.addOption(Option//
						.builder("f")//
						.longOpt("feature")//
						.hasArg(true)//
						.argName("feature name")//
						.required(false)//
						.desc("entry point for dev-helper")//
						.build());
		return options;
	}

	private static void printHelp(Options options) {
		HelpFormatter helpFormatter = new HelpFormatter();
		helpFormatter.printHelp("dvh", options, true);

		// try (PrintWriter printWriter = new PrintWriter(System.out)) {
		// helpFormatter.printUsage(printWriter, 100, "dvh", options);
		// }
	}

}
