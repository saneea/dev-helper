package io.github.saneea.feature;

import java.util.Map;
import java.util.function.Supplier;

import io.github.saneea.Feature;
import io.github.saneea.textfunction.FromBase64;
import io.github.saneea.textfunction.ToBase64;

public class Binary extends MultiFeatureBase {

	@Override
	public String getShortDescription() {
		return "binary data processing (decoding/encoding, hash, etc)";
	}

	@Override
	public Map<String, Supplier<Feature>> getFeatureAliases() {
		return new AliasesBuilder()//
				.feature("toHex", ToHex::new)//
				.feature("fromHex", FromHex::new)//
				.feature("toBase64", ToBase64::new)//
				.feature("fromBase64", FromBase64::new)//
				.feature("hash", Hash::new)//
				.feature("toFile", ToFile::new)//
				.feature("slowPipe", SlowPipe::new)//
				.build();
	}

}
