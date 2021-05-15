package io.github.saneea.dvh.feature.random

import io.github.saneea.dvh.Feature
import io.github.saneea.dvh.Feature.Meta
import io.github.saneea.dvh.FeatureContext
import org.apache.commons.cli.CommandLine
import org.apache.commons.cli.Option
import java.io.OutputStream
import java.util.Random

class RandomBytes :
    Feature,
    Feature.CLI,
    Feature.CLI.Options,
    Feature.Out.Bin.Stream {

    override lateinit var outBinStream: OutputStream
    override lateinit var commandLine: CommandLine

    override fun meta(context: FeatureContext) =
        Meta("generates random binary data")

    override fun run(context: FeatureContext) {
        val count = commandLine.getOptionValue(SIZE).toLong()
        val seed: String? = commandLine.getOptionValue(SEED)

        val r = if (seed == null)
            Random()
        else
            Random(seed.toLong())

        val bytes = ByteArray(1)

        for (i in 0 until count) {
            r.nextBytes(bytes)
            outBinStream.write(bytes)
        }
    }

    override val options: Array<Option>
        get() {
            return arrayOf(
                Option
                    .builder("s")
                    .longOpt(SIZE)
                    .hasArg(true)
                    .argName("bytes count")
                    .required(true)
                    .desc("size of generated data in bytes")
                    .build(),
                Option
                    .builder()
                    .longOpt(SEED)
                    .hasArg(true)
                    .argName("seed number")
                    .required(false)
                    .desc("seed for random generator")
                    .build()
            )
        }

    companion object {
        private const val SIZE = "size"
        private const val SEED = "seed"
    }
}