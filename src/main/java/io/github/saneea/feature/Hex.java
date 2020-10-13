package io.github.saneea.feature;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

import io.github.saneea.Feature;
import io.github.saneea.FeatureContext;
import io.github.saneea.FeatureProvider;
import io.github.saneea.textfunction.Utils;

public class Hex implements Feature {

	@Override
	public void run(FeatureContext context) throws Exception {
		Utils.dvhEntryPoint(context, new HexFeatureProvider());
	}

	@Override
	public String getShortDescription() {
		return "hex decoding/encoding";
	}

	private static class HexFeatureProvider implements FeatureProvider {

		private final Map<String, Supplier<Feature>> featureAlias;

		public HexFeatureProvider() {
			this(createFeatureAlias());
		}

		public HexFeatureProvider(Map<String, Supplier<Feature>> featureAlias) {
			this.featureAlias = featureAlias;
		}

		private static Map<String, Supplier<Feature>> createFeatureAlias() {
			Map<String, Supplier<Feature>> m = new HashMap<>();
			m.put("fromBin", ToHex::new);
			m.put("toBin", FromHex::new);
			return Collections.unmodifiableMap(m);
		}

		@Override
		public Set<String> featuresNames() {
			return featureAlias.keySet();
		}

		@Override
		public Feature createFeature(String featureName) {
			Supplier<Feature> featureCtor = featureAlias.get(featureName);
			return featureCtor != null//
					? featureCtor.get()//
					: null;
		}
	}

}
