package io.github.saneea;

import java.util.Set;

import io.github.saneea.feature.multi.FeatureInfo;

public interface FeatureProvider {
	Set<String> featuresNames();

	Feature createFeature(String featureName) throws Exception;

	default FeatureInfo featureInfo(FeatureContext context) {
		try {
			Feature feature = createFeature(context.getFeatureName());
			return new FeatureInfo(feature, feature.meta(context).description().brief());
		} catch (Exception e) {
			return new FeatureInfo(null, e.toString());
		}
	}
}
