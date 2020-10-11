package io.github.saneea;

import java.util.Map;

public abstract class FeatureProvider {
	public abstract Map<String, String> getFeatureAlias();

	public abstract Feature createFeature(String featureName) throws Exception;
}
