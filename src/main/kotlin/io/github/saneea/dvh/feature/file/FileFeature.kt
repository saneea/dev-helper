package io.github.saneea.dvh.feature.file

import io.github.saneea.dvh.Feature
import io.github.saneea.dvh.Feature.CLI
import io.github.saneea.dvh.Feature.Meta
import org.apache.commons.cli.CommandLine
import org.apache.commons.cli.Option
import java.io.File

abstract class FileFeature :
    Feature,
    CLI,
    CLI.Options {

    override lateinit var commandLine: CommandLine
    protected abstract val description: String

    protected abstract fun handleFile(file: File)

    override val meta get() = Meta(description)

    override fun run() = handleFile(File(commandLine.getOptionValue(FILE_NAME)))

    override val options = listOf(
        Option
            .builder("f")
            .longOpt(FILE_NAME)
            .hasArg(true)
            .argName("file name")
            .required(true)
            .desc("path to file")
            .build()
    )

    companion object {
        private const val FILE_NAME = "fileName"
    }
}