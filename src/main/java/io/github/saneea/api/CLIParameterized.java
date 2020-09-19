package io.github.saneea.api;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

public interface CLIParameterized {

	Options createOptions();

	void setCommandLine(CommandLine commandLine);

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
