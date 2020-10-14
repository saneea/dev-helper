package io.github.saneea.feature;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import io.github.saneea.Feature;

public class Xml extends MultiFeature {

	@Override
	public String getShortDescription() {
		return "xml processing";
	}

	@Override
	public Map<String, Supplier<Feature>> getFeatureAlias() {
		Map<String, Supplier<Feature>> m = new HashMap<>();
		m.put("toLine", XmlToLine::new);
		m.put("prettyPrint", XmlPrettyPrint::new);
		m.put("reform", XmlReform::new);
		return m;
	}

}
