package io.github.saneea.dvh.feature.file;

import java.util.Map;
import java.util.function.Supplier;

import io.github.saneea.dvh.Feature;
import io.github.saneea.dvh.FeatureContext;
import io.github.saneea.dvh.feature.multi.MultiFeatureBase;

public class File extends MultiFeatureBase {

	@Override
	public Meta meta(FeatureContext context) {
		return Meta.from("file utils");
	}

	@Override
	public Map<String, Supplier<Feature>> getFeatureAliases() {
		return new AliasesBuilder()//
				.feature("read", ReadFile::new)//
				.feature("write", WriteFile::new)//
				.feature("compare", Compare::new)//
				.build();
	}

}
