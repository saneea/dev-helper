package io.github.saneea.dvh.feature.json

import io.github.saneea.dvh.Feature
import io.github.saneea.dvh.Feature.Meta
import io.github.saneea.dvh.FeatureContext
import io.github.saneea.dvh.feature.multi.MultiFeatureBase
import io.github.saneea.dvh.utils.Const
import java.util.function.Supplier

class Json : MultiFeatureBase() {

    override fun meta(context: FeatureContext) =
        Meta.from("json processing")!!

    override fun getFeatureAliases(): Map<String, Supplier<Feature>> =
        AliasesBuilder()
            .multiFeature(
                Const.FORMAT,
                "pretty print / line",
                AliasesBuilder()
                    .feature(Const.PRETTY, JsonFormatting::Pretty)
                    .feature(Const.LINE, JsonFormatting::Line)
                    .build()
            )
            .build()
}