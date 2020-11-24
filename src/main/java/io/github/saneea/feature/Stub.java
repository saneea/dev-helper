package io.github.saneea.feature;

import io.github.saneea.Feature;
import io.github.saneea.FeatureContext;

public class Stub implements Feature {

	@Override
	public Meta meta(FeatureContext context) {
		return Meta.from("do nothing");
	}

	@Override
	public void run(FeatureContext context) {
	}

}
