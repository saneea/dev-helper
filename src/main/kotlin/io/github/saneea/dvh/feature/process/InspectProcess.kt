package io.github.saneea.dvh.feature.process

import com.google.gson.GsonBuilder
import io.github.saneea.dvh.Feature
import io.github.saneea.dvh.Feature.Meta
import org.apache.commons.cli.CommandLine
import org.apache.commons.cli.Option
import java.io.PrintStream

class InspectProcess :
    Feature,
    Feature.CLI,
    Feature.CLI.Options,
    Feature.Out.Text.PrintStream {

    override lateinit var outTextPrintStream: PrintStream
    override lateinit var commandLine: CommandLine

    override val meta = Meta("print statistic about process")

    override fun run() {
        val command = commandLine.getOptionValue(COMMAND)
        val stat = execProcess(command)
        val gson = GsonBuilder()
            .setPrettyPrinting()
            .create()
        gson.toJson(stat, outTextPrintStream)
    }

    private fun execProcess(command: String): ProcessExecutionInfo {
        val stat = ProcessExecutionInfo()
        stat.startTime = System.currentTimeMillis()
        val forkProc = Runtime.getRuntime().exec(command)
        stat.exitCode = forkProc.waitFor()
        stat.finishTime = System.currentTimeMillis()
        stat.duration = stat.finishTime - stat.startTime
        return stat
    }

    override val options = listOf(
        Option
            .builder("c")
            .longOpt(COMMAND)
            .hasArg(true)
            .argName("system command line")
            .required(true)
            .desc("e.g. 'dvh --help'")
            .build()
    )

    data class ProcessExecutionInfo(
        var exitCode: Int = 0,
        var startTime: Long = 0,
        var finishTime: Long = 0,
        var duration: Long = 0
    )

    companion object {
        private const val COMMAND = "command"
    }
}