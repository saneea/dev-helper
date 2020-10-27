package io.github.saneea.feature.text;

import java.util.Map;
import java.util.function.Supplier;

import io.github.saneea.Feature;
import io.github.saneea.feature.MultiFeatureBase;

public class Text extends MultiFeatureBase {

	@Override
	public String getShortDescription() {
		return "plain text processing";
	}

	@Override
	public Map<String, Supplier<Feature>> getFeatureAliases() {
		return new AliasesBuilder()//
				.multiFeature(//
						"case", //
						"transform to upper/lower case", //
						new AliasesBuilder()//
								.feature("upper", ConvertTextCase.Upper::new)//
								.feature("lower", ConvertTextCase.Lower::new)//
								.build())//
				.feature("split", Split::new)//
				.feature("joinLines", JoinLines::new)//
				.build();
	}
}
