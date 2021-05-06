package io.github.saneea.dvh.feature

import io.github.saneea.dvh.Feature.Meta
import io.github.saneea.dvh.FeatureContext
import io.github.saneea.dvh.RootFeatureProvider
import io.github.saneea.dvh.feature.multi.MultiFeature

class DvhRootFeature : MultiFeature() {

    private val featureProvider = RootFeatureProvider()

    override fun meta(context: FeatureContext) = Meta.from("developer helper")!!

    override fun getFeatureProvider() = featureProvider
}