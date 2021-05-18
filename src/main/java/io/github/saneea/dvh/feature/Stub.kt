package io.github.saneea.dvh.feature

import io.github.saneea.dvh.Feature
import io.github.saneea.dvh.Feature.Meta

class Stub : Feature {

    override val meta = Meta("do nothing")

    override fun run() = Unit
}