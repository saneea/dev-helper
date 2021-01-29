package io.github.saneea.dvh.feature.file;

import java.io.IOException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

import io.github.saneea.dvh.Feature;
import io.github.saneea.dvh.FeatureContext;

public abstract class FileFeature implements//
		Feature, //
		Feature.CLI, //
		Feature.CLI.Options {

	private static final String FILE_NAME = "fileName";

	private CommandLine commandLine;

	protected abstract String getDescription();

	protected abstract void handleFile(String fileName) throws IOException;

	@Override
	public Meta meta(FeatureContext context) {
		return Meta.from(getDescription());
	}

	@Override
	public void run(FeatureContext context) throws Exception {
		handleFile(commandLine.getOptionValue(FILE_NAME));
	}

	@Override
	public Option[] getOptions() {
		Option[] options = { //
				Option//
						.builder("f")//
						.longOpt(FILE_NAME)//
						.hasArg(true)//
						.argName("file name")//
						.required(true)//
						.desc("path to file")//
						.build() };

		return options;
	}

	@Override
	public void setCommandLine(CommandLine commandLine) {
		this.commandLine = commandLine;
	}

}
