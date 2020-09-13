package io.github.saneea.feature;

import java.io.PrintWriter;

import io.github.saneea.Feature;
import io.github.saneea.FeatureContext;

public class PrintArgsFeature implements Feature {

	@Override
	public void run(FeatureContext context) throws Exception {
		try (PrintWriter writer = new PrintWriter(context.getOut())) {
			for (String arg : context.getArgs()) {
				writer.println(arg);
			}
		}
	}

}
