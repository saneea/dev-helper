package io.github.saneea.dvh.feature.text

import io.github.saneea.dvh.Feature
import io.github.saneea.dvh.Feature.CLI.CommonOptions
import io.github.saneea.dvh.Feature.Meta
import io.github.saneea.dvh.FeatureContext
import org.apache.commons.cli.CommandLine
import java.io.Reader
import java.io.Writer

class ConvertTextCase {

    class Upper :
        ConvertTextCaseBase(
            Character::toUpperCase,
            Character::toUpperCase
        ) {

        override val meta get() = Meta("convert text to upper case (aBcDe -> ABCDE)")
    }

    class Lower :
        ConvertTextCaseBase(
            Character::toLowerCase,
            Character::toLowerCase
        ) {

        override val meta get() = Meta("convert text to lower case (aBcDe -> abcde)")
    }
}

abstract class ConvertTextCaseBase(
    private val charConverter: (Char) -> Char,
    private val intConverter: (Int) -> Int
) :
    Feature,
    Feature.CLI,
    Feature.In.Text.Reader,
    Feature.Out.Text.Writer {

    override lateinit var context: FeatureContext
    override lateinit var inTextReader: Reader
    override lateinit var outTextWriter: Writer
    override lateinit var commandLine: CommandLine

    override fun run() {
        if (commandLine.hasOption(CommonOptions.NON_BUFFERED_STREAMS)) {
            var codePoint: Int
            while (inTextReader.read().also { codePoint = it } != -1) {
                outTextWriter.write(intConverter(codePoint))
                outTextWriter.flush()
            }
        } else {
            val buf = CharArray(4096)
            var wasRead: Int
            while (inTextReader.read(buf).also { wasRead = it } != -1) {
                convertChars(buf, wasRead)
                outTextWriter.write(buf, 0, wasRead)
            }
        }
    }

    private fun convertChars(buf: CharArray, size: Int) {
        for (i in 0 until size) {
            buf[i] = charConverter(buf[i])
        }
    }
}