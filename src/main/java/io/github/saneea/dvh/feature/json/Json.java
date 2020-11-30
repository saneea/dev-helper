package io.github.saneea.dvh.feature.json;

import java.util.Map;
import java.util.function.Supplier;

import io.github.saneea.dvh.Feature;
import io.github.saneea.dvh.FeatureContext;
import io.github.saneea.dvh.feature.multi.MultiFeatureBase;
import io.github.saneea.dvh.utils.Const;

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
