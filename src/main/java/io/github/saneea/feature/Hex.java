package io.github.saneea.feature;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import io.github.saneea.Feature;

public class Hex extends MultiFeature {

	@Override
	public String getShortDescription() {
		return "hex decoding/encoding";
	}

	@Override
	public Map<String, Supplier<Feature>> getFeatureAlias() {
		Map<String, Supplier<Feature>> m = new HashMap<>();
		m.put("fromBin", ToHex::new);
		m.put("toBin", FromHex::new);
		return m;
	}

}
