package io.github.saneea.dvh.feature.random;

import java.util.Map;
import java.util.function.Supplier;

import io.github.saneea.dvh.Feature;
import io.github.saneea.dvh.FeatureContext;
import io.github.saneea.dvh.feature.multi.MultiFeatureBase;

public class Random extends MultiFeatureBase {

	@Override
	public Meta meta(FeatureContext context) {
		return Meta.from("generate random data");
	}

	@Override
	public Map<String, Supplier<Feature>> getFeatureAliases() {
		return new AliasesBuilder()//
				.feature("bytes", RandomBytes::new)//
				.feature("uuid", UUID::new)//
				.build();
	}

}
