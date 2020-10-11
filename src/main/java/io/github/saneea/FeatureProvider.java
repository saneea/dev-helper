package io.github.saneea;

import java.util.Set;

public interface FeatureProvider {
	Set<String> featuresNames();

	Feature createFeature(String featureName) throws Exception;
}
