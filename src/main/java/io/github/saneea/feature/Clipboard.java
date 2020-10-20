package io.github.saneea.feature;

import java.util.Map;
import java.util.function.Supplier;

import io.github.saneea.Feature;

public class Clipboard extends MultiFeature {

	@Override
	public String getShortDescription() {
		return "read/write clipboard (text only)";
	}

	@Override
	public Map<String, Supplier<Feature>> getFeatureAliases() {
		return new AliasesBuilder()//
				.feature("read", TextFromClipboard::new)//
				.feature("write", TextToClipboard::new)//
				.build();
	}

}
