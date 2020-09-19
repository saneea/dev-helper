package io.github.saneea.feature;

import java.io.OutputStreamWriter;
import java.io.Writer;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;

import io.github.saneea.Feature;
import io.github.saneea.FeatureContext;
import io.github.saneea.api.CLIParameterized;

public class UUID implements Feature, CLIParameterized {

	private CommandLine commandLine;

	@Override
	public String getShortDescription() {
		return "generate new UUID";
	}

	@Override
	public void run(FeatureContext context) throws Exception {
		String outputEncoding = commandLine.getOptionValue(CommonOptions.OUTPUT_ENCODING);
		try (Writer writer = outputEncoding == null//
				? new OutputStreamWriter(context.getOut())//
				: new OutputStreamWriter(context.getOut(), outputEncoding)) {
			writer.write(java.util.UUID.randomUUID().toString());
		}
	}

	@Override
	public Options createOptions() {
		return new Options()//
				.addOption(CommonOptions.OUTPUT_ENCODING_OPTION);
	}

	@Override
	public void setCommandLine(CommandLine commandLine) {
		this.commandLine = commandLine;
	}

}
