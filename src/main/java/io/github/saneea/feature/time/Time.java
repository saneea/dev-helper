package io.github.saneea.feature.time;

import java.util.Map;
import java.util.function.Supplier;

import io.github.saneea.Feature;
import io.github.saneea.FeatureContext;
import io.github.saneea.feature.multi.MultiFeatureBase;

public class Time extends MultiFeatureBase {

	@Override
	public Meta meta(FeatureContext context) {
		return Meta.from("time utils");
	}

	@Override
	public Map<String, Supplier<Feature>> getFeatureAliases() {
		return new AliasesBuilder()//
				.feature("now", Now::new)//
				.feature("convert", Converter::new)//
				.build();
	}

}
