package io.github.saneea.feature;

import java.io.PrintStream;

import io.github.saneea.Feature;
import io.github.saneea.FeatureContext;

public class PrintArgsFeature implements Feature, Feature.Out.Text.PrintStream {

	private PrintStream out;

	@Override
	public String getShortDescription() {
		return "print CLI args";
	}

	@Override
	public void run(FeatureContext context) throws Exception {
		for (String arg : context.getArgs()) {
			out.println(arg);
		}
	}

	@Override
	public void setOut(PrintStream out) {
		this.out = out;
	}

}
