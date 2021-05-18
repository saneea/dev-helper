package io.github.saneea.dvh.feature.time.format

import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime

class EpochFormat(
    private val epochToInstant: (Long) -> Instant,
    private val instantToEpoch: (Instant) -> Long
) : Format {

    override fun parse(time: String): ZonedDateTime {
        val timeAsNumber = time.toLong()
        val timeAsInstant = epochToInstant(timeAsNumber)
        return ZonedDateTime.ofInstant(timeAsInstant, ZoneId.systemDefault())
    }

    override fun render(time: ZonedDateTime): String {
        val timeAsInstant = time.toInstant()
        val timeAsNumber = instantToEpoch(timeAsInstant)
        return timeAsNumber.toString()
    }
}