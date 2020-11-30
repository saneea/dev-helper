package io.github.saneea.dvh.feature.binary;

import java.util.Map;
import java.util.function.Supplier;

import io.github.saneea.dvh.Feature;
import io.github.saneea.dvh.FeatureContext;
import io.github.saneea.dvh.feature.binary.base64.FromBase64;
import io.github.saneea.dvh.feature.binary.base64.ToBase64;
import io.github.saneea.dvh.feature.binary.gzip.FromGzip;
import io.github.saneea.dvh.feature.binary.gzip.ToGzip;
import io.github.saneea.dvh.feature.binary.hex.FromHex;
import io.github.saneea.dvh.feature.binary.hex.ToHex;
import io.github.saneea.dvh.feature.multi.MultiFeatureBase;

public class Binary extends MultiFeatureBase {

	@Override
	public Meta meta(FeatureContext context) {
		return Meta.from("binary data processing (decoding/encoding, hash, etc)");
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
								.feature("gzip", ToGzip::new)//
								.build())//
				.multiFeature(//
						"from", //
						"create output from...", //
						new AliasesBuilder()//
								.feature("hex", FromHex::new)//
								.feature("base64", FromBase64::new)//
								.feature("gzip", FromGzip::new)//
								.build())//
				.build();
	}

}
