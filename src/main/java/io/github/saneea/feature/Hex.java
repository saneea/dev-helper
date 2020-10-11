package io.github.saneea.feature;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

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

	private static class HexFeatureProvider implements FeatureProvider {

		private static final String TO_BIN = "toBin";
		private static final String FROM_BIN = "fromBin";

		private static final Set<String> FEATURE_NAMES = new HashSet<>(Arrays.asList(TO_BIN, FROM_BIN));

		@Override
		public Set<String> featuresNames() {
			return FEATURE_NAMES;
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
