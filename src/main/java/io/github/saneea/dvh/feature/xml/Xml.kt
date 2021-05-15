package io.github.saneea.dvh.feature.xml

import io.github.saneea.dvh.Feature.Meta
import io.github.saneea.dvh.FeatureContext
import io.github.saneea.dvh.feature.multi.FeaturesCreators
import io.github.saneea.dvh.feature.multi.MultiFeatureBase
import io.github.saneea.dvh.utils.const.FORMAT
import io.github.saneea.dvh.utils.const.LINE
import io.github.saneea.dvh.utils.const.PRETTY

class Xml : MultiFeatureBase() {

    override val meta get() = Meta("xml processing")

    override fun getFeatureAliases(): FeaturesCreators =
        AliasesBuilder()
            .multiFeature(
                FORMAT,
                "pretty print / line",
                AliasesBuilder()
                    .feature(PRETTY, ::XmlPrettyPrint)
                    .feature(LINE, ::XmlToLine)
                    .build()
            )
            .feature("compare", ::FilteredComparison)
            .build()
}