package io.github.saneea.dvh.feature.clipboard

import io.github.saneea.dvh.Feature.Meta
import io.github.saneea.dvh.FeatureContext
import io.github.saneea.dvh.feature.multi.FeaturesCreators
import io.github.saneea.dvh.feature.multi.MultiFeatureBase

class Clipboard : MultiFeatureBase() {

    override fun meta(context: FeatureContext) = Meta("read/write clipboard (text only)")

    override fun getFeatureAliases(): FeaturesCreators {
        return AliasesBuilder()
            .feature("read", ::TextFromClipboard)
            .feature("write", ::TextToClipboard)
            .build()
    }
}