package io.github.saneea.dvh.feature.binary

import io.github.saneea.dvh.Feature
import io.github.saneea.dvh.Feature.CLI
import io.github.saneea.dvh.Feature.Meta
import io.github.saneea.dvh.FeatureContext
import org.apache.commons.cli.CommandLine
import org.apache.commons.cli.Option
import java.io.InputStream
import java.io.OutputStream

class SlowPipe :
    Feature,
    CLI,
    CLI.Options,
    Feature.In.Bin.Stream,
    Feature.Out.Bin.Stream {

    private lateinit var `in`: InputStream
    private lateinit var out: OutputStream
    private lateinit var commandLine: CommandLine

    override fun meta(context: FeatureContext) =
        Meta.from("transfer bytes with delay")

    override fun run(context: FeatureContext) {
        val delay = commandLine.getOptionValue(DELAY).toLong()
        var byteCode: Int
        while (`in`.read().also { byteCode = it } != -1) {
            Thread.sleep(delay)
            out.write(byteCode)
        }
    }

    override fun setIn(`in`: InputStream) {
        this.`in` = `in`
    }

    override fun setOut(out: OutputStream) {
        this.out = out
    }

    override fun setCommandLine(commandLine: CommandLine) {
        this.commandLine = commandLine
    }

    override fun getOptions(): Array<Option> {
        return arrayOf(
            Option
                .builder("d")
                .longOpt(DELAY)
                .hasArg(true)
                .argName("millis")
                .required(true)
                .desc("delay before each byte")
                .build()
        )
    }

    companion object {
        private const val DELAY = "delay"
    }
}