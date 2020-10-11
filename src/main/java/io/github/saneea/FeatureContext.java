package io.github.saneea;

public class FeatureContext {
	final String[] args;
	final FeatureProvider featureProvider;

	public FeatureContext(String[] args, FeatureProvider featureProvider) {
		this.args = args;
		this.featureProvider = featureProvider;
	}

	public String[] getArgs() {
		return args;
	}

	public FeatureProvider getFeatureProvider() {
		return featureProvider;
	}

}
