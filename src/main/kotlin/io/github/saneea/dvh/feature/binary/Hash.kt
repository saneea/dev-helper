package io.github.saneea.dvh.feature.binary

import io.github.saneea.dvh.Feature
import io.github.saneea.dvh.Feature.CLI
import io.github.saneea.dvh.Feature.Meta
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

    override lateinit var commandLine: CommandLine
    override lateinit var inBinStream: InputStream
    override lateinit var outTextPrintStream: PrintStream

    override val meta = Meta("calc hash (md5, sha-* etc.)")

    override fun run() {
        val alg = commandLine.getOptionValue(Params.ALGORITHM)
        execute(inBinStream, outTextPrintStream, alg)
    }

    override val options = Params.createOptions()

    object Params {
        const val ALGORITHM = "algorithm"
        fun createOptions() = listOf(
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