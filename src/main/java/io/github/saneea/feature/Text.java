package io.github.saneea.feature;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import io.github.saneea.Feature;

public class Text extends MultiFeature {

	@Override
	public String getShortDescription() {
		return "plain text processing";
	}

	@Override
	public Map<String, Supplier<Feature>> getFeatureAlias() {
		Map<String, Supplier<Feature>> m = new HashMap<>();
		m.put("case", TextCase::new);
		return m;
	}

	private static class TextCase extends MultiFeature {

		@Override
		public String getShortDescription() {
			return "transform to upper/lower case";
		}

		@Override
		public Map<String, Supplier<Feature>> getFeatureAlias() {
			Map<String, Supplier<Feature>> m = new HashMap<>();
			m.put("upper", UpperCase::new);
			return m;
		}

	}
}
