package io.github.saneea.feature.xml;

import java.util.Map;
import java.util.function.Supplier;

import io.github.saneea.Feature;
import io.github.saneea.FeatureContext;
import io.github.saneea.feature.multi.MultiFeatureBase;

public class Xml extends MultiFeatureBase {

	@Override
	public Meta meta(FeatureContext context) {
		return Meta.from("xml processing");
	}

	@Override
	public Map<String, Supplier<Feature>> getFeatureAliases() {
		return new AliasesBuilder()//
				.feature("toLine", XmlToLine::new)//
				.feature("prettyPrint", XmlPrettyPrint::new)//
				.feature("reform", XmlReform::new)//
				.build();
	}

}
