package io.github.saneea.dvh.feature.file

import io.github.saneea.dvh.Feature.Meta
import io.github.saneea.dvh.FeatureContext
import io.github.saneea.dvh.feature.multi.FeaturesCreators
import io.github.saneea.dvh.feature.multi.MultiFeatureBase

class File : MultiFeatureBase() {

    override val meta get() = Meta("file utils")

    override fun getFeatureAliases(): FeaturesCreators =
        AliasesBuilder()
            .feature("read", ::ReadFile)
            .feature("write", ::WriteFile)
            .feature("compare", ::Compare)
            .build()
}