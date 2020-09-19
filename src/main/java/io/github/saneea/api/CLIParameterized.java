package io.github.saneea.api;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

public interface CLIParameterized {

	Option[] EMPTY_OPTIONS_ARRAY = {};

	default Option[] getOptions() {
		return EMPTY_OPTIONS_ARRAY;
	}

	default void setCommandLine(CommandLine commandLine) {
	}

	interface CommonOptions {

		String OUTPUT_ENCODING = "outputEncoding";

		Option OUTPUT_ENCODING_OPTION = Option//
				.builder("oe")//
				.longOpt(OUTPUT_ENCODING)//
				.hasArg(true)//
				.argName("encoding")//
				.required(false)//
				.desc("output encoding")//
				.build();
	}
}
