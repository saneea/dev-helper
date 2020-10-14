package io.github.saneea.feature;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import io.github.saneea.Feature;

public class Random extends MultiFeature {

	@Override
	public String getShortDescription() {
		return "generate random data";
	}

	@Override
	public Map<String, Supplier<Feature>> getFeatureAlias() {
		Map<String, Supplier<Feature>> m = new HashMap<>();
		m.put("bytes", RandomBytes::new);
		m.put("uuid", UUID::new);
		return m;
	}

}
