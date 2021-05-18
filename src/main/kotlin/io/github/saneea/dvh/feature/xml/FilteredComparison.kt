package io.github.saneea.dvh.feature.xml

import io.github.saneea.dvh.Feature
import io.github.saneea.dvh.Feature.Meta
import io.github.saneea.dvh.feature.xml.XmlPrettyPrint.Companion.run
import org.apache.commons.cli.CommandLine
import org.apache.commons.cli.Option
import java.io.*
import java.nio.charset.StandardCharsets

class FilteredComparison :
    Feature,
    Feature.CLI,
    Feature.CLI.Options {

    override lateinit var commandLine: CommandLine

    override val meta = Meta("run external comparison tool for 'pretty printed' xml files")

    override fun run() {
        val comparisonTool = commandLine.getOptionValue(Params.COMPARISON_TOOL)
        val leftFileName = commandLine.getOptionValue(Params.LEFT)
        val rightFileName = commandLine.getOptionValue(Params.RIGHT)

        var leftFilteredFileName: String
        var rightFilteredFileName: String
        try {
            leftFilteredFileName = getFilteredFile(leftFileName)
            rightFilteredFileName = getFilteredFile(rightFileName)
        } catch (e: Exception) {
            leftFilteredFileName = leftFileName
            rightFilteredFileName = rightFileName
        }

        val processBuilder = ProcessBuilder(comparisonTool, leftFilteredFileName, rightFilteredFileName)
        val process = processBuilder.start()
        process.waitFor()
    }

    object Params {
        var COMPARISON_TOOL = "comparisonTool"
        var LEFT = "left"
        var RIGHT = "right"

        fun createOptions(): Array<Option> {
            return arrayOf(
                Option
                    .builder("ct")
                    .longOpt(COMPARISON_TOOL)
                    .hasArg(true)
                    .argName("system command")
                    .required(true)
                    .desc("system command for comparison of just path to extrenal comparison tool")
                    .build(),
                Option
                    .builder("l")
                    .longOpt(LEFT)
                    .hasArg(true)
                    .argName("file path")
                    .required(true)
                    .desc("left side file for comparison")
                    .build(),
                Option
                    .builder("r")
                    .longOpt(RIGHT)
                    .hasArg(true)
                    .argName("file path")
                    .required(true)
                    .desc("right side file for comparison")
                    .build()
            )
        }
    }

    override val options get() = Params.createOptions()

    private fun getFilteredFile(inputFileName: String): String {
        val outputFile = File.createTempFile("filtered", null)
        outputFile.deleteOnExit()
        val outputFileName = outputFile.path
        InputStreamReader(
            BufferedInputStream(
                FileInputStream(
                    inputFileName
                )
            ),
            StandardCharsets.UTF_8
        ).use { `in` ->
            OutputStreamWriter(
                BufferedOutputStream(
                    FileOutputStream(
                        outputFile
                    )
                ),
                StandardCharsets.UTF_8
            ).use { out -> run(`in`, out, false) }
        }
        return outputFileName
    }

}