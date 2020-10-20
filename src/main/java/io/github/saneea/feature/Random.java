package io.github.saneea.feature;

import java.util.Map;
import java.util.function.Supplier;

import io.github.saneea.Feature;

public class Random extends MultiFeature {

	@Override
	public String getShortDescription() {
		return "generate random data";
	}

	@Override
	public Map<String, Supplier<Feature>> getFeatureAlias() {
		return new AliasesBuilder()//
				.feature("bytes", RandomBytes::new)//
				.feature("uuid", UUID::new)//
				.build();
	}

}
