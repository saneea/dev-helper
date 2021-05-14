package io.github.saneea.dvh.feature.text

import io.github.saneea.dvh.Feature
import io.github.saneea.dvh.Feature.Meta
import io.github.saneea.dvh.FeatureContext
import org.apache.commons.cli.CommandLine
import org.apache.commons.cli.Option
import java.io.PrintStream
import java.io.Reader

class Split :
    Feature,
    Feature.CLI,
    Feature.CLI.Options,
    Feature.In.Text.Reader,
    Feature.Out.Text.PrintStream {

    private lateinit var `in`: Reader
    private lateinit var out: PrintStream
    private lateinit var commandLine: CommandLine

    override fun meta(context: FeatureContext) = Meta("split text as lines")

    override fun run(context: FeatureContext) {
        val size = lineSize
        while (transferLine(size)) {
            // no code
        }
    }

    private fun transferLine(size: Long): Boolean {
        for (i in 0 until size) {
            val charCode = `in`.read()
            if (charCode == -1) {
                if (i != 0L) {
                    out.println()
                }
                return false
            }
            out.print(charCode.toChar())
        }
        out.println()
        return true
    }

    private val lineSize: Long
        get() {
            val sizeStr: String? = commandLine.getOptionValue(SIZE_OPT)
            val size: Long
            if (sizeStr == null) {
                size = SIZE_OPT_DEFAULT
            } else {
                size = sizeStr.toLong()
                require(size > 0) { "Parameter $SIZE_OPT must be greater than 0" }
            }
            return size
        }

    override fun setInTextReader(`in`: Reader) {
        this.`in` = `in`
    }

    override fun setOutTextPrintStream(out: PrintStream) {
        this.out = out
    }

    override fun setCommandLine(commandLine: CommandLine) {
        this.commandLine = commandLine
    }

    override fun getOptions(): Array<Option> {
        return arrayOf(
            Option
                .builder("s")
                .longOpt(SIZE_OPT)
                .hasArg(true)
                .argName("line size")
                .required(false)
                .desc("size limit for each line (default: $SIZE_OPT_DEFAULT)")
                .build()
        )
    }

    companion object {
        private const val SIZE_OPT_DEFAULT = 60L
        private const val SIZE_OPT = "size"
    }
}