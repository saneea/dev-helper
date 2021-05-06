package io.github.saneea.dvh.feature.file

import io.github.saneea.dvh.Feature
import io.github.saneea.dvh.Feature.Meta
import io.github.saneea.dvh.FeatureContext
import io.github.saneea.dvh.feature.multi.MultiFeatureBase
import java.util.function.Supplier

class File : MultiFeatureBase() {

    override fun meta(context: FeatureContext) =
        Meta.from("file utils")!!

    //TODO: change "Supplier<Feature>" to "() -> Feature"
    override fun getFeatureAliases(): Map<String, Supplier<Feature>> =
        AliasesBuilder()
            .feature("read", ::ReadFile)
            .feature("write", ::WriteFile)
            .feature("compare", ::Compare)
            .build()
}