package io.github.saneea.dvh.feature.time.format

import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.function.LongFunction
import java.util.function.ToLongFunction

class EpochFormat(
    private val epochToInstant: LongFunction<Instant>,
    private val instantToEpoch: ToLongFunction<Instant>
) : Format {

    override fun parse(time: String): ZonedDateTime {
        val timeAsNumber = java.lang.Long.valueOf(time)
        val timeAsInstant = epochToInstant.apply(timeAsNumber)
        return ZonedDateTime.ofInstant(timeAsInstant, ZoneId.systemDefault())
    }

    override fun render(time: ZonedDateTime): String {
        val timeAsInstant = time.toInstant()
        val timeAsNumber = instantToEpoch.applyAsLong(timeAsInstant)
        return timeAsNumber.toString()
    }
}