package io.github.saneea.dvh.feature;

import java.io.IOException;

import io.github.saneea.dvh.FeatureContext;
import io.github.saneea.dvh.FeatureProvider;
import io.github.saneea.dvh.RootFeatureProvider;
import io.github.saneea.dvh.feature.multi.MultiFeature;

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
