package io.github.saneea.dvh.feature.clipboard;

import java.util.Map;
import java.util.function.Supplier;

import io.github.saneea.dvh.Feature;
import io.github.saneea.dvh.FeatureContext;
import io.github.saneea.dvh.feature.multi.MultiFeatureBase;

public class Clipboard extends MultiFeatureBase {

	@Override
	public Meta meta(FeatureContext context) {
		return Meta.from("read/write clipboard (text only)");
	}

	@Override
	public Map<String, Supplier<Feature>> getFeatureAliases() {
		return new AliasesBuilder()//
				.feature("read", TextFromClipboard::new)//
				.feature("write", TextToClipboard::new)//
				.build();
	}

}
