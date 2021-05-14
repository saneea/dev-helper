package io.github.saneea.dvh.feature.binary.hex

import io.github.saneea.dvh.Feature
import io.github.saneea.dvh.Feature.Meta
import io.github.saneea.dvh.FeatureContext
import java.io.InputStream
import java.io.PrintStream

class ToHex :
    Feature,
    Feature.In.Bin.Stream,
    Feature.Out.Text.PrintStream {

    private lateinit var `in`: InputStream
    private lateinit var out: PrintStream

    override fun meta(context: FeatureContext) = Meta("convert input binary sequence to hex")

    override fun run(context: FeatureContext) {
        run(`in`, out)
    }

    override fun setIn(`in`: InputStream) {
        this.`in` = `in`
    }

    override fun setOut(out: PrintStream) {
        this.out = out
    }

    companion object {
        fun run(input: InputStream, out: PrintStream) {
            var charCode: Int
            while (input.read().also { charCode = it } != -1) {
                val hexString = Integer.toHexString(charCode)
                if (hexString.length < 2) {
                    out.print('0')
                }
                out.print(hexString)
            }
        }
    }
}