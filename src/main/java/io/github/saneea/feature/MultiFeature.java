package io.github.saneea.feature;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

import io.github.saneea.Feature;
import io.github.saneea.FeatureContext;
import io.github.saneea.FeatureProvider;
import io.github.saneea.textfunction.Utils;

public abstract class MultiFeature implements Feature {

	@Override
	public void run(FeatureContext context) throws Exception {
		Utils.dvhEntryPoint(//
				context, //
				new MultiFeatureProvider(//
						Collections.unmodifiableMap(//
								getFeatureAlias())));
	}

	public abstract Map<String, Supplier<Feature>> getFeatureAlias();

	private static class MultiFeatureProvider implements FeatureProvider {

		private final Map<String, Supplier<Feature>> featureAlias;

		public MultiFeatureProvider(Map<String, Supplier<Feature>> featureAlias) {
			this.featureAlias = featureAlias;
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
