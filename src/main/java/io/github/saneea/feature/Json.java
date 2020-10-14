package io.github.saneea.feature;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import io.github.saneea.Feature;

public class Json extends MultiFeature {

	@Override
	public String getShortDescription() {
		return "json processing";
	}

	@Override
	public Map<String, Supplier<Feature>> getFeatureAlias() {
		Map<String, Supplier<Feature>> m = new HashMap<>();
		m.put("prettyPrint", JsonPrettyPrint::new);
		return m;
	}

}
