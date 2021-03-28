package io.github.saneea.dvh.feature.clipboard

import io.github.saneea.dvh.Feature
import io.github.saneea.dvh.Feature.Meta
import io.github.saneea.dvh.FeatureContext
import io.github.saneea.dvh.feature.multi.MultiFeatureBase
import java.util.function.Supplier

class Clipboard : MultiFeatureBase() {

    override fun meta(context: FeatureContext) =
        Meta.from("read/write clipboard (text only)")!!

    override fun getFeatureAliases(): Map<String, Supplier<Feature>> {
        return AliasesBuilder()
            .feature("read", ::TextFromClipboard)
            .feature("write", ::TextToClipboard)
            .build()
    }
}