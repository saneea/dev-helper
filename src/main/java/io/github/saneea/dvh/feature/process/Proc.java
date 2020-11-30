package io.github.saneea.dvh.feature.process;

import java.util.Map;
import java.util.function.Supplier;

import io.github.saneea.dvh.Feature;
import io.github.saneea.dvh.FeatureContext;
import io.github.saneea.dvh.feature.multi.MultiFeatureBase;

public class Proc extends MultiFeatureBase {

	@Override
	public Meta meta(FeatureContext context) {
		return Meta.from("fork process");
	}

	@Override
	public Map<String, Supplier<Feature>> getFeatureAliases() {
		return new AliasesBuilder()//
				.feature("inspect", InspectProcess::new)//
				.feature("toFile", ToFile::new)//
				.build();
	}
}
