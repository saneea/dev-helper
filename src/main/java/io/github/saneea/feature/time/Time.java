package io.github.saneea.feature.time;

import java.util.Map;
import java.util.function.Supplier;

import io.github.saneea.Feature;
import io.github.saneea.feature.MultiFeatureBase;

public class Time extends MultiFeatureBase {

	@Override
	public String getShortDescription() {
		return "time utils";
	}

	@Override
	public Map<String, Supplier<Feature>> getFeatureAliases() {
		return new AliasesBuilder()//
				.feature("now", Now::new)//
				.build();
	}

}
