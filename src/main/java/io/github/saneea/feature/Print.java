package io.github.saneea.feature;

import java.util.Map;
import java.util.function.Supplier;

import io.github.saneea.Feature;

public class Print extends MultiFeatureBase {

	@Override
	public String getShortDescription() {
		return "just print text from CLI args";
	}

	@Override
	public Map<String, Supplier<Feature>> getFeatureAliases() {
		return new AliasesBuilder()//
				.feature("args", PrintArgsFeature::new)//
				.feature("line", PrintLine::new)//
				.build();
	}

}
