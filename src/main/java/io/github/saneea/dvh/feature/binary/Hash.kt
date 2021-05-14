package io.github.saneea.dvh.feature.binary

import io.github.saneea.dvh.Feature
import io.github.saneea.dvh.Feature.CLI
import io.github.saneea.dvh.Feature.Meta
import io.github.saneea.dvh.FeatureContext
import io.github.saneea.dvh.feature.binary.hex.ToHex
import org.apache.commons.cli.CommandLine
import org.apache.commons.cli.Option
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.io.PrintStream
import java.security.MessageDigest

class Hash :
    Feature,
    CLI,
    CLI.Options,
    Feature.In.Bin.Stream,
    Feature.Out.Text.PrintStream {

    private lateinit var commandLine: CommandLine
    private lateinit var `in`: InputStream
    private lateinit var out: PrintStream

    override fun meta(context: FeatureContext) = Meta("calc hash (md5, sha-* etc.)")

    override fun run(context: FeatureContext) {
        val alg = commandLine.getOptionValue(Params.ALGORITHM)
        execute(`in`, out, alg)
    }

    override fun getOptions() = Params.createOptions()

    override fun setCommandLine(commandLine: CommandLine) {
        this.commandLine = commandLine
    }

    object Params {
        const val ALGORITHM = "algorithm"
        fun createOptions(): Array<Option> {
            return arrayOf(
                Option
                    .builder("a")
                    .longOpt(ALGORITHM)
                    .hasArg(true)
                    .argName("algorithm name")
                    .required(true)
                    .desc("hash algorithm (e.g. md5)")
                    .build()
            )
        }
    }

    override fun setOutTextPrintStream(out: PrintStream) {
        this.out = out
    }

    override fun setInBinStream(`in`: InputStream) {
        this.`in` = `in`
    }

    companion object {
        private const val BUFFER_SIZE = 4096

        private fun execute(input: InputStream, out: PrintStream, alg: String) {
            val md = MessageDigest.getInstance(alg)
            val buf = ByteArray(BUFFER_SIZE)
            var len: Int
            while (input.read(buf).also { len = it } != -1) {
                md.update(buf, 0, len)
            }
            ToHex.run(ByteArrayInputStream(md.digest()), out)
        }
    }
}