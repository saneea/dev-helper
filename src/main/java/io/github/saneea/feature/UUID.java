package io.github.saneea.feature;

import io.github.saneea.Feature;
import io.github.saneea.Feature.Util.IOConsumer;
import io.github.saneea.FeatureContext;

public class UUID implements Feature, Feature.CLI, Feature.Out.Text.String {

	private IOConsumer<String> out;

	@Override
	public String getShortDescription() {
		return "generate new UUID";
	}

	@Override
	public void run(FeatureContext context) throws Exception {
		out.accept(java.util.UUID.randomUUID().toString());
	}

	@Override
	public void setOut(IOConsumer<String> out) {
		this.out = out;
	}

}
