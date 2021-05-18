package io.github.saneea.dvh.feature.random

import io.github.saneea.dvh.Feature
import io.github.saneea.dvh.Feature.Meta
import io.github.saneea.dvh.StringConsumer
import java.util.UUID

class UUID :
    Feature,
    Feature.Out.Text.String {

    override lateinit var outTextString: StringConsumer

    override val meta = Meta("generate new UUID")

    override fun run() =
        outTextString(UUID.randomUUID().toString())
}