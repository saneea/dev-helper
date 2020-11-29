package io.github.saneea.feature.xml;

import java.util.Map;
import java.util.function.Supplier;

import io.github.saneea.Feature;
import io.github.saneea.FeatureContext;
import io.github.saneea.feature.multi.MultiFeatureBase;
import io.github.saneea.utils.Const;

public class Xml extends MultiFeatureBase {

	@Override
	public Meta meta(FeatureContext context) {
		return Meta.from("xml processing");
	}

	@Override
	public Map<String, Supplier<Feature>> getFeatureAliases() {
		return new AliasesBuilder()//
				.multiFeature(Const.FORMAT, //
						"pretty print / line", //
						new AliasesBuilder()//
								.feature(Const.PRETTY, XmlPrettyPrint::new)//
								.feature(Const.LINE, XmlToLine::new)//
								.build())//
				.feature("compare", FilteredComparison::new)//
				.build();
	}

}
