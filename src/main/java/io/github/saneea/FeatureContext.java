package io.github.saneea;

import java.util.stream.Stream;

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

	public Stream<String> getFeaturesChain() {
		return getFeaturesChain(this);
	}

	private static Stream<String> getFeaturesChain(FeatureContext context) {
		if (context == null) {
			return Stream.empty();
		}

		Parent parent = context.getParent();
		FeatureContext parentContext = parent != null//
				? parent.getContext()//
				: null;

		return Stream.concat(//
				getFeaturesChain(parentContext), //
				Stream.of(context.getFeatureName()));
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
