package io.github.saneea.feature.random;

import java.util.Map;
import java.util.function.Supplier;

import io.github.saneea.Feature;
import io.github.saneea.FeatureContext;
import io.github.saneea.feature.multi.MultiFeatureBase;

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
