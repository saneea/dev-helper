package io.github.saneea;

public class FeatureContext {

	private final Parent parent;
	private final String featureName;
	private final String[] args;

	public FeatureContext(Parent parent, String featureName, String[] args) {
		this.parent = parent;
		this.featureName = featureName;
		this.args = args;
	}

	public Parent getParent() {
		return parent;
	}

	public String getFeatureName() {
		return featureName;
	}

	public String[] getArgs() {
		return args;
	}

	public static class Parent {
		private final FeatureContext context;
		private final FeatureProvider featureProvider;

		public Parent(FeatureContext context, FeatureProvider featureProvider) {
			this.context = context;
			this.featureProvider = featureProvider;
		}

		public FeatureContext getContext() {
			return context;
		}

		public FeatureProvider getFeatureProvider() {
			return featureProvider;
		}
	}

}
