package io.github.saneea.dvh.feature.random;

import io.github.saneea.dvh.Feature;
import io.github.saneea.dvh.Feature.Util.IOConsumer;
import io.github.saneea.dvh.FeatureContext;

public class UUID implements Feature, Feature.Out.Text.String {

	private IOConsumer<String> out;

	@Override
	public Meta meta(FeatureContext context) {
		return Meta.from("generate new UUID");
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
