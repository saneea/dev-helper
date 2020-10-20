package io.github.saneea.feature;

import java.util.Map;
import java.util.function.Supplier;

import io.github.saneea.Feature;

public class Json extends MultiFeature {

	@Override
	public String getShortDescription() {
		return "json processing";
	}

	@Override
	public Map<String, Supplier<Feature>> getFeatureAliases() {
		return new AliasesBuilder()//
				.multiFeature("formatting", //
						"pretty print / line", //
						new AliasesBuilder()//
								.feature("pretty", JsonFormatting.Pretty::new)//
								.feature("line", JsonFormatting.Line::new)//
								.build())//
				.build();
	}

}
