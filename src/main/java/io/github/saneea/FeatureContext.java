package io.github.saneea;

import java.util.stream.Stream;

public class FeatureContext {

	private final FeatureContext parent;
	private final String featureName;
	private final String[] args;

	public FeatureContext(FeatureContext parent, String featureName, String[] args) {
		this.parent = parent;
		this.featureName = featureName;
		this.args = args;
	}

	public FeatureContext getParent() {
		return parent;
	}

	public String getFeatureName() {
		return featureName;
	}

	public String[] getArgs() {
		return args;
	}

	public Stream<String> getFeaturesChain() {
		return Stream.concat(//
				parent != null//
						? parent.getFeaturesChain()//
						: Stream.empty(), //
				Stream.of(getFeatureName()));
	}

}
