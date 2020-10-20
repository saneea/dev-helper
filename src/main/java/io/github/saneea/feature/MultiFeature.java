package io.github.saneea.feature;

import java.util.Collections;
import java.util.LinkedHashMap;
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

	public static class AliasesBuilder {
		private final Map<String, Supplier<Feature>> aliases = new LinkedHashMap<>();

		public AliasesBuilder feature(String featureAlias, Supplier<Feature> featureCtor) {
			aliases.put(featureAlias, featureCtor);
			return this;
		}

		public AliasesBuilder multiFeature(//
				String featureAlias, //
				String shortDescription, //
				Map<String, Supplier<Feature>> children) {
			return feature(featureAlias, () -> new MultiFeature() {

				@Override
				public String getShortDescription() {
					return shortDescription;
				}

				@Override
				public Map<String, Supplier<Feature>> getFeatureAlias() {
					return children;
				}

			});
		}

		public Map<String, Supplier<Feature>> build() {
			return aliases;
		}
	}

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
