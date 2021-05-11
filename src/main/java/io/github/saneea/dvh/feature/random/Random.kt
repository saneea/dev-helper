package io.github.saneea.dvh.feature.random

import io.github.saneea.dvh.Feature
import io.github.saneea.dvh.Feature.Meta
import io.github.saneea.dvh.FeatureContext
import io.github.saneea.dvh.feature.multi.MultiFeatureBase
import java.util.function.Supplier

class Random : MultiFeatureBase() {
    override fun meta(context: FeatureContext) =
        Meta.from("generate random data")

    override fun getFeatureAliases(): Map<String, Supplier<Feature>> =
        AliasesBuilder()
            .feature("bytes", ::RandomBytes)
            .feature("uuid", ::UUID)
            .build()
}