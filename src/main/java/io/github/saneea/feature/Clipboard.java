package io.github.saneea.feature;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import io.github.saneea.Feature;

public class Clipboard extends MultiFeature {

	@Override
	public String getShortDescription() {
		return "read/write clipboard (text only)";
	}

	@Override
	public Map<String, Supplier<Feature>> getFeatureAlias() {
		Map<String, Supplier<Feature>> m = new HashMap<>();
		m.put("read", TextFromClipboard::new);
		m.put("write", TextToClipboard::new);
		return m;
	}

}
