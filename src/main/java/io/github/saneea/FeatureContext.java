package io.github.saneea;

public class FeatureContext {
	private final FeatureContext parentContext;
	private final String featureName;
	private final String[] args;
	private final FeatureProvider parentFeatureProvider;

	public FeatureContext(FeatureContext parentContext, String featureName, String[] args,
			FeatureProvider parentFeatureProvider) {
		this.parentContext = parentContext;
		this.featureName = featureName;
		this.args = args;
		this.parentFeatureProvider = parentFeatureProvider;
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

	public FeatureProvider getParentFeatureProvider() {
		return parentFeatureProvider;
	}

}
