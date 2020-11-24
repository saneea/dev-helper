package io.github.saneea.feature.multi;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

import io.github.saneea.Feature;
import io.github.saneea.FeatureContext;
import io.github.saneea.FeatureProvider;

public abstract class MultiFeatureBase extends MultiFeature {

	@Override
	public FeatureProvider getFeatureProvider() {
		return new MultiFeatureProvider(//
				Collections.unmodifiableMap(//
						getFeatureAliases()));
	}

	public abstract Map<String, Supplier<Feature>> getFeatureAliases();

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
			return feature(featureAlias, () -> new MultiFeatureBase() {

				@Override
				public Meta meta(FeatureContext context) {
					return Meta.from(shortDescription);
				}

				@Override
				public Map<String, Supplier<Feature>> getFeatureAliases() {
					return children;
				}

			});
		}

		public Map<String, Supplier<Feature>> build() {
			return aliases;
		}
	}

	private static class MultiFeatureProvider implements FeatureProvider {

		private final Map<String, Supplier<Feature>> featureAliases;

		public MultiFeatureProvider(Map<String, Supplier<Feature>> featureAliases) {
			this.featureAliases = featureAliases;
		}

		@Override
		public Set<String> featuresNames() {
			return featureAliases.keySet();
		}

		@Override
		public Feature createFeature(String featureName) {
			Supplier<Feature> featureCtor = featureAliases.get(featureName);
			return featureCtor != null//
					? featureCtor.get()//
					: null;
		}
	}

}
