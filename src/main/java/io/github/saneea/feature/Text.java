package io.github.saneea.feature;

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
		return new AliasesBuilder()//
				.multiFeature(//
						"case", //
						"transform to upper/lower case", //
						new AliasesBuilder()//
								.feature("upper", ConvertTextCase.Upper::new)//
								.feature("lower", ConvertTextCase.Lower::new)//
								.build())//
				.build();
	}
}
