package io.github.saneea.dvh.feature.xml

import io.github.saneea.dvh.Feature
import io.github.saneea.dvh.Feature.Meta
import io.github.saneea.dvh.FeatureContext
import io.github.saneea.dvh.feature.multi.MultiFeatureBase
import io.github.saneea.dvh.utils.const.FORMAT
import io.github.saneea.dvh.utils.const.LINE
import io.github.saneea.dvh.utils.const.PRETTY
import java.util.function.Supplier

class Xml : MultiFeatureBase() {

    override fun meta(context: FeatureContext) = Meta.from("xml processing")!!

    override fun getFeatureAliases(): Map<String, Supplier<Feature>> =
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