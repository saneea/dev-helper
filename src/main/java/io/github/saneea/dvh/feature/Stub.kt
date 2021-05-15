package io.github.saneea.dvh.feature

import io.github.saneea.dvh.Feature
import io.github.saneea.dvh.Feature.Meta
import io.github.saneea.dvh.FeatureContext

class Stub : Feature {

    override lateinit var context: FeatureContext

    override fun meta() = Meta("do nothing")

    override fun run() = Unit
}