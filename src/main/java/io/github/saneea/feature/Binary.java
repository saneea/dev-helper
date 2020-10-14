package io.github.saneea.feature;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import io.github.saneea.Feature;
import io.github.saneea.textfunction.FromBase64;
import io.github.saneea.textfunction.ToBase64;

public class Binary extends MultiFeature {

	@Override
	public String getShortDescription() {
		return "binary data processing (decoding/encoding, hash, etc)";
	}

	@Override
	public Map<String, Supplier<Feature>> getFeatureAlias() {
		Map<String, Supplier<Feature>> m = new HashMap<>();
		m.put("toHex", ToHex::new);
		m.put("fromHex", FromHex::new);
		m.put("toBase64", ToBase64::new);
		m.put("fromBase64", FromBase64::new);
		m.put("hash", Hash::new);
		m.put("toFile", ToFile::new);
		m.put("slowPipe", SlowPipe::new);
		return m;
	}

}
