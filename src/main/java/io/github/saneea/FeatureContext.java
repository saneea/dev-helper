package io.github.saneea;

public class FeatureContext {
	private final FeatureContext parentContext;
	private final String featureName;
	private final String[] args;
	private final FeatureProvider featureProvider;

	public FeatureContext(FeatureContext parentContext, String featureName, String[] args,
			FeatureProvider featureProvider) {
		this.parentContext = parentContext;
		this.featureName = featureName;
		this.args = args;
		this.featureProvider = featureProvider;
	}

	public FeatureContext getParentContext() {
		return parentContext;
	}

	public String getFeatureName() {
		return featureName;
	}

	public String[] getArgs() {
		return args;
	}

	public FeatureProvider getFeatureProvider() {
		return featureProvider;
	}

}
