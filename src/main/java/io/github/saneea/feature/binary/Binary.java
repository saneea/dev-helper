package io.github.saneea.feature.binary;

import java.util.Map;
import java.util.function.Supplier;

import io.github.saneea.Feature;
import io.github.saneea.feature.multi.MultiFeatureBase;

public class Binary extends MultiFeatureBase {

	@Override
	public String getShortDescription() {
		return "binary data processing (decoding/encoding, hash, etc)";
	}

	@Override
	public Map<String, Supplier<Feature>> getFeatureAliases() {
		return new AliasesBuilder()//
				.feature("hash", Hash::new)//
				.feature("slowPipe", SlowPipe::new)//
				.multiFeature(//
						"to", //
						"convert input to...", //
						new AliasesBuilder()//
								.feature("hex", ToHex::new)//
								.feature("base64", ToBase64::new)//
								.build())//
				.multiFeature(//
						"from", //
						"create output from...", //
						new AliasesBuilder()//
								.feature("hex", FromHex::new)//
								.feature("base64", FromBase64::new)//
								.build())//
				.build();
	}

}
