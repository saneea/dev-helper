package io.github.saneea.dvh.feature.print

import io.github.saneea.dvh.Feature.Meta
import io.github.saneea.dvh.FeatureContext
import io.github.saneea.dvh.feature.multi.FeaturesCreators
import io.github.saneea.dvh.feature.multi.MultiFeatureBase

class Print : MultiFeatureBase() {
    override fun meta() = Meta("just print text from CLI args")

    override fun getFeatureAliases(): FeaturesCreators =
        AliasesBuilder()
            .feature("args", ::PrintArgsFeature)
            .feature("line", ::PrintLine)
            .build()
}