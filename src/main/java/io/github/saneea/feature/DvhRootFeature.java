package io.github.saneea.feature;

import java.io.IOException;

import io.github.saneea.FeatureProvider;
import io.github.saneea.RootFeatureProvider;
import io.github.saneea.feature.multi.MultiFeature;

public class DvhRootFeature extends MultiFeature {

	private final FeatureProvider featureProvider;

	public DvhRootFeature() throws IOException {
		this.featureProvider = new RootFeatureProvider();
	}

	@Override
	public String getShortDescription() {
		return "developer helper";
	}

	@Override
	public FeatureProvider getFeatureProvider() {
		return featureProvider;
	}

}
