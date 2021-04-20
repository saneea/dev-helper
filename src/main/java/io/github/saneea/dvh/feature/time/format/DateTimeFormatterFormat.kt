package io.github.saneea.dvh.feature.time.format

import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class DateTimeFormatterFormat(private val formatter: DateTimeFormatter) : Format {

    override fun parse(time: String) = ZonedDateTime.from(formatter.parse(time))!!

    override fun render(time: ZonedDateTime) = formatter.format(time)!!
}