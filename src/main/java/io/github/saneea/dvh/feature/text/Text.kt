package io.github.saneea.dvh.feature.text

import io.github.saneea.dvh.Feature.Meta
import io.github.saneea.dvh.FeatureContext
import io.github.saneea.dvh.feature.multi.FeaturesCreators
import io.github.saneea.dvh.feature.multi.MultiFeatureBase

class Text : MultiFeatureBase() {

    override val meta get() = Meta("plain text processing")

    override fun getFeatureAliases(): FeaturesCreators =
        AliasesBuilder()
            .multiFeature(
                "case",
                "transform to upper/lower case",
                AliasesBuilder()
                    .feature("up", ConvertTextCase::Upper)
                    .feature("down", ConvertTextCase::Lower)
                    .build()
            )
            .multiFeature(
                "chunker",
                "split text to chunks or join chunks again",
                AliasesBuilder()
                    .feature("split", ::Split)
                    .feature("join", ::JoinLines)
                    .build()
            )
            .feature("toCharArray", ::ToCharArray)
            .feature("trim", ::Trim)
            .feature("newLine", ::AddNewLine)
            .feature("pipe", ::Pipe)
            .feature("slash", ::SlashReplacer)
            .build()
}