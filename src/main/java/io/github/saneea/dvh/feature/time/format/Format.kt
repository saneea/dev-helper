package io.github.saneea.dvh.feature.time.format

import java.time.ZonedDateTime

interface Format {
    fun parse(time: String): ZonedDateTime
    fun render(time: ZonedDateTime): String
}