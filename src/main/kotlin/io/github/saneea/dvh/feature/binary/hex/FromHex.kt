package io.github.saneea.dvh.feature.binary.hex

import io.github.saneea.dvh.Feature
import io.github.saneea.dvh.Feature.Meta
import io.github.saneea.dvh.FeatureContext
import java.io.OutputStream
import java.io.Reader

private const val HEX_DIGITS_IN_BYTE = 2

class FromHex :
    Feature,
    Feature.In.Text.Reader,
    Feature.Out.Bin.Stream {

    private lateinit var reader: Reader
    override lateinit var outBinStream: OutputStream

    override fun meta(context: FeatureContext) = Meta("convert input hex sequence to binary")


    override fun run(context: FeatureContext) {
        while (true) {
            val buf = readCharsFromStream(reader)
            if (buf.size < HEX_DIGITS_IN_BYTE) {
                break
            }

            val digits = buf.map { Character.digit(it, 16) }
            val byteCode = (digits[0] shl 4) + digits[1]

            outBinStream.write(byteCode)
        }
    }

    private fun readCharsFromStream(reader: Reader): List<Char> {
        val outChars: MutableList<Char> = ArrayList(HEX_DIGITS_IN_BYTE)
        val tempBuf = CharArray(HEX_DIGITS_IN_BYTE)
        while (outChars.size < HEX_DIGITS_IN_BYTE) {
            val len = reader.read(tempBuf)
            if (len == -1) {
                break
            }
            for (i in 0 until len) {
                outChars.add(tempBuf[i])
            }
        }
        return outChars
    }

    override fun setInTextReader(`in`: Reader) {
        this.reader = `in`
    }
}