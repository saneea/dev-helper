package io.github.saneea.dvh.feature.json

import io.github.saneea.dvh.Feature
import io.github.saneea.dvh.Feature.Meta
import io.github.saneea.dvh.FeatureContext
import io.github.saneea.dvh.feature.multi.MultiFeatureBase
import io.github.saneea.dvh.utils.const.FORMAT
import io.github.saneea.dvh.utils.const.LINE
import io.github.saneea.dvh.utils.const.PRETTY
import java.util.function.Supplier

class Json : MultiFeatureBase() {

    override fun meta(context: FeatureContext) =
        Meta.from("json processing")

    override fun getFeatureAliases(): Map<String, Supplier<Feature>> =
        AliasesBuilder()
            .multiFeature(
                FORMAT,
                "pretty print / line",
                AliasesBuilder()
                    .feature(PRETTY, JsonFormatting::Pretty)
                    .feature(LINE, JsonFormatting::Line)
                    .build()
            )
            .build()
}