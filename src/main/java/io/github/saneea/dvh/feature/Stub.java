package io.github.saneea.dvh.feature;

import io.github.saneea.dvh.Feature;
import io.github.saneea.dvh.FeatureContext;

public class Stub implements Feature {

	@Override
	public Meta meta(FeatureContext context) {
		return Meta.from("do nothing");
	}

	@Override
	public void run(FeatureContext context) {
	}

}
