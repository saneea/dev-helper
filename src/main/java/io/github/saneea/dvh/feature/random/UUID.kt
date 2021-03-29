package io.github.saneea.dvh.feature.random

import io.github.saneea.dvh.Feature
import io.github.saneea.dvh.Feature.Meta
import io.github.saneea.dvh.Feature.Util.IOConsumer
import io.github.saneea.dvh.FeatureContext
import java.util.UUID

class UUID :
    Feature,
    Feature.Out.Text.String {

    private lateinit var out: IOConsumer<String>

    override fun meta(context: FeatureContext) =
        Meta.from("generate new UUID")!!

    override fun run(context: FeatureContext) {
        out.accept(UUID.randomUUID().toString())
    }

    override fun setOut(out: IOConsumer<String>) {
        this.out = out
    }
}