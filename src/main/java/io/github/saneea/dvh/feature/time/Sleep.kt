package io.github.saneea.dvh.feature.time

import io.github.saneea.dvh.Feature
import io.github.saneea.dvh.Feature.Meta
import io.github.saneea.dvh.FeatureContext
import org.apache.commons.cli.CommandLine
import org.apache.commons.cli.Option
import org.apache.commons.cli.PatternOptionBuilder
import java.util.concurrent.TimeUnit
import java.util.stream.Collectors
import java.util.stream.Stream

class Sleep :
    Feature,
    Feature.ContextAware,
    Feature.CLI,
    Feature.CLI.Options {

    private enum class Units(
        val cli: String,
        val display: String,
        val javaTimeUnit: TimeUnit
    ) {

        MS("ms", "millisecond", TimeUnit.MILLISECONDS),
        S("s", "second", TimeUnit.SECONDS),
        M("m", "minute", TimeUnit.MINUTES),
        H("h", "hour", TimeUnit.HOURS),
        D("d", "day", TimeUnit.DAYS);

        companion object {
            val DEFAULT: Units = S
            fun stream(): Stream<Units> {
                return Stream.of(*values())
            }

            fun fromCli(optValue: String): Units {
                return stream()
                    .filter { unit -> unit.cli == optValue }
                    .findAny()
                    .orElseThrow { IllegalArgumentException("Illegal units type: $optValue") }
            }
        }
    }

    override lateinit var context: FeatureContext
    override lateinit var commandLine: CommandLine

    override val meta: Meta
        get() {
            val execWithD = "${context.featuresChainString} -$DURATION_SHORT"
            return Meta(
                Meta.Description(
                    "wait some time before exit"
                ),
                listOf(
                    Meta.Example(
                        "wait 5 seconds",
                        "$execWithD 5"
                    ),
                    Meta.Example(
                        "wait 5000 milliseconds (5 seconds)",
                        "$execWithD 5000 -$UNITS_SHORT ${Units.MS.cli}"
                    )
                )
            )
        }

    override fun run() {
        val timeUnitStr: String = commandLine.getOptionValue(UNITS, Units.DEFAULT.cli)
        val timeUnit: TimeUnit = Units.fromCli(timeUnitStr).javaTimeUnit
        val durationStr: String = commandLine.getOptionValue(DURATION)
        val duration: Long = durationStr.toLong()
        timeUnit.sleep(duration)
    }

    override val options
        get() = arrayOf(
            Option
                .builder(DURATION_SHORT)
                .longOpt(DURATION)
                .hasArg(true)
                .argName("duration")
                .required(true)
                .desc("time duration")
                .type(PatternOptionBuilder.NUMBER_VALUE)
                .build(),
            Option
                .builder(UNITS_SHORT)
                .longOpt(UNITS)
                .hasArg(true)
                .argName(
                    Units.stream()
                        .map { u: Units -> u.cli }
                        .collect(Collectors.joining("|"))
                )
                .required(false)
                .desc(
                    ("time units, default is '" + Units.DEFAULT.cli + "' (" +
                            Units.stream()
                                .map { u: Units -> "${u.cli} - ${u.display}" }
                                .collect(Collectors.joining(", "))
                            + ")")
                )
                .build()
        )

    companion object {
        private const val DURATION = "duration"
        private const val DURATION_SHORT = "d"
        private const val UNITS = "units"
        private const val UNITS_SHORT = "u"
    }
}