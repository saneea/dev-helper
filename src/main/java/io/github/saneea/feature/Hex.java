package io.github.saneea.feature;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import io.github.saneea.Feature;
import io.github.saneea.FeatureContext;
import io.github.saneea.FeatureProvider;
import io.github.saneea.textfunction.Utils;

public class Hex implements Feature {

	@Override
	public void run(FeatureContext context) throws Exception {
		Utils.dvhEntryPoint(context.getArgs(), new HexFeatureProvider());
	}

	@Override
	public String getShortDescription() {
		return "hex decoding/encoding";
	}

	private static class HexFeatureProvider extends FeatureProvider {

		private static final String TO_BIN = "toBin";
		private static final String FROM_BIN = "fromBin";

		@Override
		public Map<String, String> getFeatureAlias() {
			Map<String, String> m = new HashMap<>();
			m.put(TO_BIN, "stub");
			m.put(FROM_BIN, "stub");
			return Collections.unmodifiableMap(m);
		}

		@Override
		public Feature createFeature(String featureName) {
			switch (featureName) {
			case TO_BIN:
				return new FromHex();
			case FROM_BIN:
				return new ToHex();
			}
			throw new IllegalArgumentException("Unknown feature: \"" + featureName + "\"");
		}
	}

}
