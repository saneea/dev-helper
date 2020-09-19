package io.github.saneea.feature;

import java.io.PrintStream;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;

import io.github.saneea.Feature;
import io.github.saneea.FeatureContext;
import io.github.saneea.api.CLIParameterized;
import io.github.saneea.api.PrintStreamOutputable;

public class UUID implements Feature, CLIParameterized, PrintStreamOutputable {

	private PrintStream out;

	@Override
	public String getShortDescription() {
		return "generate new UUID";
	}

	@Override
	public void run(FeatureContext context) throws Exception {
		out.print(java.util.UUID.randomUUID().toString());
	}

	@Override
	public Options createOptions() {
		return new Options();
	}

	@Override
	public void setCommandLine(CommandLine commandLine) {
	}

	@Override
	public void setPrintStreamOut(PrintStream out) {
		this.out = out;
	}

}
