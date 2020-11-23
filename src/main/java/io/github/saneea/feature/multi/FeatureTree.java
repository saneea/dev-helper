package io.github.saneea.feature.multi;

import java.util.ArrayList;
import java.util.List;

public class FeatureTree {

	private final String alias;
	private final String description;

	private final List<FeatureTree> children = new ArrayList<>();

	public FeatureTree(String alias, String description) {
		this.alias = alias;
		this.description = description;
	}

	public String getAlias() {
		return alias;
	}

	public String getDescription() {
		return description;
	}

	public List<FeatureTree> getChildren() {
		return children;
	}

}
