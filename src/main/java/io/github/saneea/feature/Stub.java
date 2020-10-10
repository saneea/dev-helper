package io.github.saneea.feature;

import io.github.saneea.Feature;
import io.github.saneea.FeatureContext;

public class Stub implements Feature {

	@Override
	public String getShortDescription() {
		return "do nothing";
	}

	@Override
	public void run(FeatureContext context) {
	}

}
