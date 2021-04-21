package io.github.saneea.dvh.feature.xml

import io.github.saneea.dvh.Feature
import io.github.saneea.dvh.Feature.Meta
import io.github.saneea.dvh.FeatureContext
import io.github.saneea.dvh.feature.multi.MultiFeatureBase
import io.github.saneea.dvh.utils.Const
import java.util.function.Supplier

class Xml : MultiFeatureBase() {

    override fun meta(context: FeatureContext) = Meta.from("xml processing")!!

    override fun getFeatureAliases(): Map<String, Supplier<Feature>> =
        AliasesBuilder()
            .multiFeature(
                Const.FORMAT,
                "pretty print / line",
                AliasesBuilder()
                    .feature(Const.PRETTY, ::XmlPrettyPrint)
                    .feature(Const.LINE, ::XmlToLine)
                    .build()
            )
            .feature("compare", ::FilteredComparison)
            .build()
}