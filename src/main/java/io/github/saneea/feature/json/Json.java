package io.github.saneea.feature.json;

import java.util.Map;
import java.util.function.Supplier;

import io.github.saneea.Feature;
import io.github.saneea.FeatureContext;
import io.github.saneea.feature.multi.MultiFeatureBase;
import io.github.saneea.utils.Const;

public class Json extends MultiFeatureBase {

	@Override
	public Meta meta(FeatureContext context) {
		return Meta.from("json processing");
	}

	@Override
	public Map<String, Supplier<Feature>> getFeatureAliases() {
		return new AliasesBuilder()//
				.multiFeature(Const.FORMAT, //
						"pretty print / line", //
						new AliasesBuilder()//
								.feature(Const.PRETTY, JsonFormatting.Pretty::new)//
								.feature(Const.LINE, JsonFormatting.Line::new)//
								.build())//
				.build();
	}

}
