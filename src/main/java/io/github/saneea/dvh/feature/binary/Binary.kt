package io.github.saneea.dvh.feature.binary

import io.github.saneea.dvh.Feature
import io.github.saneea.dvh.Feature.Meta
import io.github.saneea.dvh.FeatureContext
import io.github.saneea.dvh.feature.binary.base64.FromBase64
import io.github.saneea.dvh.feature.binary.base64.ToBase64
import io.github.saneea.dvh.feature.binary.gzip.FromGzip
import io.github.saneea.dvh.feature.binary.gzip.ToGzip
import io.github.saneea.dvh.feature.binary.hex.FromHex
import io.github.saneea.dvh.feature.binary.hex.ToHex
import io.github.saneea.dvh.feature.multi.MultiFeatureBase
import java.util.function.Supplier

class Binary : MultiFeatureBase() {
    override fun meta(context: FeatureContext) =
        Meta.from("binary data processing (decoding/encoding, hash, etc)")

    override fun getFeatureAliases(): Map<String, Supplier<Feature>> {
        return AliasesBuilder()
            .feature("hash", ::Hash)
            .feature("slowPipe", ::SlowPipe)
            .multiFeature(
                "to",
                "convert input to...",
                AliasesBuilder()
                    .feature("hex", ::ToHex)
                    .feature("base64", ::ToBase64)
                    .feature("gzip", ::ToGzip)
                    .feature("randomArt", ::ToRandomArt)
                    .build()
            )
            .multiFeature(
                "from",
                "create output from...",
                AliasesBuilder()
                    .feature("hex", ::FromHex)
                    .feature("base64", ::FromBase64)
                    .feature("gzip", ::FromGzip)
                    .build()
            )
            .build()
    }
}