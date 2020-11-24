package io.github.saneea.feature.print;

import java.io.PrintStream;

import io.github.saneea.Feature;
import io.github.saneea.FeatureContext;

public class PrintArgsFeature implements Feature, Feature.Out.Text.PrintStream {

	private PrintStream out;

	@Override
	public Meta meta(FeatureContext context) {
		return Meta.from("print CLI args");
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
