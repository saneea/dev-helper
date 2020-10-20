package io.github.saneea.feature;

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
		return new AliasesBuilder()//
				.feature("toLine", XmlToLine::new)//
				.feature("prettyPrint", XmlPrettyPrint::new)//
				.feature("reform", XmlReform::new)//
				.build();
	}

}
