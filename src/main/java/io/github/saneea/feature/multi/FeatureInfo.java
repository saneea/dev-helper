package io.github.saneea.feature.multi;

import io.github.saneea.Feature;

public class FeatureInfo {
	public final Feature feature;
	public final String description;

	public FeatureInfo(Feature feature, String description) {
		this.feature = feature;
		this.description = description;
	}
}