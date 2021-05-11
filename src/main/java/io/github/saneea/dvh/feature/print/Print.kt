package io.github.saneea.dvh.feature.print

import io.github.saneea.dvh.Feature
import io.github.saneea.dvh.Feature.Meta
import io.github.saneea.dvh.FeatureContext
import io.github.saneea.dvh.feature.multi.MultiFeatureBase
import java.util.function.Supplier

class Print : MultiFeatureBase() {
    override fun meta(context: FeatureContext) =
        Meta.from("just print text from CLI args")

    override fun getFeatureAliases(): Map<String, Supplier<Feature>> =
        AliasesBuilder()
            .feature("args", ::PrintArgsFeature)
            .feature("line", ::PrintLine)
            .build()
}