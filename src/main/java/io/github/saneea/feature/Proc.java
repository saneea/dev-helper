package io.github.saneea.feature;

import java.util.Map;
import java.util.function.Supplier;

import io.github.saneea.Feature;

public class Proc extends MultiFeatureBase {

	@Override
	public String getShortDescription() {
		return "fork process";
	}

	@Override
	public Map<String, Supplier<Feature>> getFeatureAliases() {
		return new AliasesBuilder()//
				.feature("inspect", InspectProcess::new)//
				.feature("toFile", ToFile::new)//
				.feature("filteredComparison", FilteredComparison::new)//
				.build();
	}
}
