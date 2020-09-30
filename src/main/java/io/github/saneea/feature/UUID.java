package io.github.saneea.feature;

import java.io.PrintStream;

import io.github.saneea.Feature;
import io.github.saneea.FeatureContext;

public class UUID implements Feature, Feature.CLI, Feature.Out.Text.PrintStream {

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
	public void setOut(PrintStream out) {
		this.out = out;
	}

}
