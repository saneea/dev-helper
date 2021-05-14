package io.github.saneea.dvh.feature.binary

import io.github.saneea.dvh.Feature
import io.github.saneea.dvh.Feature.CLI
import io.github.saneea.dvh.Feature.Meta
import io.github.saneea.dvh.FeatureContext
import org.apache.commons.cli.CommandLine
import org.apache.commons.cli.Option
import java.io.IOException
import java.io.InputStream
import java.io.PrintStream

class ToRandomArt : Feature, CLI, CLI.Options, Feature.In.Bin.Stream, Feature.Out.Text.PrintStream {

    private lateinit var `in`: InputStream
    private lateinit var out: PrintStream
    private lateinit var commandLine: CommandLine

    private var sizeX = 0
    private var sizeY = 0

    private var currentX = 0
    private var currentY = 0

    private lateinit var picture: Picture

    override fun meta(context: FeatureContext) = Meta("convert input binary sequence to random art picture")

    @Throws(IOException::class)
    override fun run(context: FeatureContext) {
        sizeX = commandLine.getOptionValue(SIZE_X, DEFAULT_SIZE_X.toString()).toInt()
        sizeY = commandLine.getOptionValue(SIZE_Y, DEFAULT_SIZE_Y.toString()).toInt()
        currentX = sizeX / 2
        currentY = sizeY / 2
        picture = Picture(sizeX, sizeY)
        var byteCode: Int
        while (`in`.read().also { byteCode = it } != -1) {
            handleInputByte(byteCode)
        }
        printHorizontalBorder(out)
        for (row in picture.rows) {
            printVerticalBorder(out)
            for (element in row.elements) {
                out.print(intToAsciiPixel(element))
            }
            printVerticalBorder(out)
            out.println()
        }
        printHorizontalBorder(out)
    }

    private fun printVerticalBorder(out: PrintStream) {
        out.print('|')
    }

    private fun printHorizontalBorder(out: PrintStream) {
        out.print('+')
        for (col in 0 until sizeX) {
            out.print('-')
        }
        out.print('+')
        out.println()
    }

    private fun intToAsciiPixel(element: Int): Char {
        return PICTURE_ALPHABET[element % PICTURE_ALPHABET.size]
    }

    private fun handleInputByte(byteCode: Int) {
        val bits = byteToBits(byteCode)
        for (i in 0..3) {
            handleDirection(bits[i * 2], bits[i * 2 + 1])
        }
    }

    private fun byteToBits(byteCode: Int): BooleanArray {
        var byteCodeTmp = byteCode
        val out = BooleanArray(8)
        for (i in out.indices.reversed()) {
            out[i] = byteCodeTmp % 2 != 0
            byteCodeTmp = byteCodeTmp shr 1
        }
        return out
    }

    private fun handleDirection(horizontal: Boolean, direction: Boolean) {
        if (horizontal) {
            currentX = changeCurrentPos(currentX, direction, sizeX)
        } else {
            currentY = changeCurrentPos(currentY, direction, sizeY)
        }
        picture.rows[currentY].elements[currentX]++
    }

    private fun changeCurrentPos(current: Int, direction: Boolean, pictureSize: Int): Int {
        var ret = current
        ret += if (direction) 1 else -1
        if (ret >= pictureSize) {
            ret = 0
        } else if (ret < 0) {
            ret = pictureSize - 1
        }
        return ret
    }

    override fun setIn(`in`: InputStream) {
        this.`in` = `in`
    }

    override fun setOut(out: PrintStream) {
        this.out = out
    }

    override fun getOptions(): Array<Option> {
        return arrayOf(
            createSizeOption("x", SIZE_X, "width", DEFAULT_SIZE_X),
            createSizeOption("y", SIZE_Y, "height", DEFAULT_SIZE_Y)
        )
    }

    private fun createSizeOption(
        shortOptionName: String,
        longOptionName: String,
        displayName: String,
        defaultValue: Int
    ): Option {
        return Option
            .builder(shortOptionName)
            .longOpt(longOptionName)
            .hasArg(true)
            .argName(displayName)
            .required(false)
            .desc("picture $displayName (default $defaultValue)")
            .build()
    }

    override fun setCommandLine(commandLine: CommandLine) {
        this.commandLine = commandLine
    }

    private class Picture(sizeX: Int, sizeY: Int) {
        val rows: Array<Row> = Array(sizeY) { Row(sizeX) }

        class Row(size: Int) {
            val elements: IntArray = IntArray(size)
        }

    }

    companion object {
        private const val SIZE_X = "sizeX"
        private const val SIZE_Y = "sizeY"
        private const val DEFAULT_SIZE_X = 20
        private const val DEFAULT_SIZE_Y = 10
        private val PICTURE_ALPHABET = charArrayOf(
            ' ', '.', 'o', '+', 'X', '#', 'H'
        )
    }
}