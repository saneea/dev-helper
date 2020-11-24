package io.github.saneea.feature;

import java.io.IOException;

import io.github.saneea.FeatureContext;
import io.github.saneea.FeatureProvider;
import io.github.saneea.RootFeatureProvider;
import io.github.saneea.feature.multi.MultiFeature;

public class DvhRootFeature extends MultiFeature {

	private final FeatureProvider featureProvider;

	public DvhRootFeature() throws IOException {
		this.featureProvider = new RootFeatureProvider();
	}

	@Override
	public Meta meta(FeatureContext context) {
		return Meta.from("developer helper");
	}

	@Override
	public FeatureProvider getFeatureProvider() {
		return featureProvider;
	}

}
