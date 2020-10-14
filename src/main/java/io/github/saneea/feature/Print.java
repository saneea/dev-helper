package io.github.saneea.feature;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import io.github.saneea.Feature;

public class Print extends MultiFeature {

	@Override
	public String getShortDescription() {
		return "just print text from CLI args";
	}

	@Override
	public Map<String, Supplier<Feature>> getFeatureAlias() {
		Map<String, Supplier<Feature>> m = new HashMap<>();
		m.put("args", PrintArgsFeature::new);
		m.put("line", PrintLine::new);
		return m;
	}

}
