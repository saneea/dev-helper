package io.github.saneea.dvh.feature.json

import io.github.saneea.dvh.Feature.Meta
import io.github.saneea.dvh.FeatureContext
import io.github.saneea.dvh.feature.multi.FeaturesCreators
import io.github.saneea.dvh.feature.multi.MultiFeatureBase
import io.github.saneea.dvh.utils.const.FORMAT
import io.github.saneea.dvh.utils.const.LINE
import io.github.saneea.dvh.utils.const.PRETTY

class Json : MultiFeatureBase() {

    override fun meta(context: FeatureContext) = Meta("json processing")

    override fun getFeatureAliases(): FeaturesCreators =
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