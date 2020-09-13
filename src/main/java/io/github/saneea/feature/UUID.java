package io.github.saneea.feature;

import java.io.OutputStreamWriter;
import java.io.Writer;

import io.github.saneea.Feature;
import io.github.saneea.FeatureContext;

public class UUID implements Feature {

	@Override
	public void run(FeatureContext context) throws Exception {
		try (Writer writer = new OutputStreamWriter(context.getOut())) {
			writer.write(java.util.UUID.randomUUID().toString());
		}
	}

}
