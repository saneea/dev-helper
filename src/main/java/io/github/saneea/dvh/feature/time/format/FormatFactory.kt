package io.github.saneea.dvh.feature.time.format

import java.time.Instant
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.stream.Collectors
import java.util.stream.Stream

const val FORMAT_HUMAN = "human"
private const val FORMAT_UNIX = "unix"
private const val FORMAT_JAVA = "java"
private const val FORMAT_ISO = "ISO"

class FormatFactory {

    private val formats: Map<String, Format>

    fun createFormat(formatNameOrPattern: String) =
        formats[formatNameOrPattern]
            ?: DateTimeFormatterFormat(DateTimeFormatter.ofPattern(formatNameOrPattern))

    val availableFormatsString: String
        get() = Stream
            .concat(
                formats.keys.stream(),
                Stream.of("<pattern>")
            )
            .collect(Collectors.joining("|"))

    init {
        val f: MutableMap<String, Format> = LinkedHashMap()

        f[FORMAT_UNIX] = EpochFormat(
            { epochSecond: Long -> Instant.ofEpochSecond(epochSecond) },
            { obj: Instant -> obj.epochSecond }
        )

        f[FORMAT_JAVA] = EpochFormat(
            { epochMilli: Long -> Instant.ofEpochMilli(epochMilli) },
            { obj: Instant -> obj.toEpochMilli() }
        )

        f[FORMAT_HUMAN] = DateTimeFormatterFormat(DateTimeFormatter.RFC_1123_DATE_TIME)
        f[FORMAT_ISO] = DateTimeFormatterFormat(DateTimeFormatter.ISO_OFFSET_DATE_TIME)

        formats = Collections.unmodifiableMap(f)
    }
}