package io.github.saneea;

import java.util.Set;

import io.github.saneea.feature.multi.FeatureInfo;

public interface FeatureProvider {
	Set<String> featuresNames();

	Feature createFeature(String featureName) throws Exception;

	default FeatureInfo featureInfo(String featureName) {
		try {
			Feature feature = createFeature(featureName);
			return new FeatureInfo(feature, feature.getShortDescription());
		} catch (Exception e) {
			return new FeatureInfo(null, e.toString());
		}
	}
}
