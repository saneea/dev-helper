package io.github.saneea.dvh.feature.print;

import java.util.Map;
import java.util.function.Supplier;

import io.github.saneea.dvh.Feature;
import io.github.saneea.dvh.FeatureContext;
import io.github.saneea.dvh.feature.multi.MultiFeatureBase;

public class Print extends MultiFeatureBase {

	@Override
	public Meta meta(FeatureContext context) {
		return Meta.from("just print text from CLI args");
	}

	@Override
	public Map<String, Supplier<Feature>> getFeatureAliases() {
		return new AliasesBuilder()//
				.feature("args", PrintArgsFeature::new)//
				.feature("line", PrintLine::new)//
				.build();
	}

}
