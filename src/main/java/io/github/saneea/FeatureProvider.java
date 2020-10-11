package io.github.saneea;

import java.util.Properties;

public abstract class FeatureProvider {
	public abstract Properties getFeatureAlias();

	public abstract Feature createFeature(String featureName) throws Exception;
}
