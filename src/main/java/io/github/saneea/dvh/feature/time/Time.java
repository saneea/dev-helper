package io.github.saneea.dvh.feature.time;

import java.util.Map;
import java.util.function.Supplier;

import io.github.saneea.dvh.Feature;
import io.github.saneea.dvh.FeatureContext;
import io.github.saneea.dvh.feature.multi.MultiFeatureBase;

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
				.feature("sleep", Sleep::new)//
				.build();
	}

}
