package io.github.saneea.dvh.feature.process

import com.google.gson.GsonBuilder
import io.github.saneea.dvh.Feature
import io.github.saneea.dvh.Feature.Meta
import io.github.saneea.dvh.FeatureContext
import org.apache.commons.cli.CommandLine
import org.apache.commons.cli.Option
import java.io.PrintStream

class InspectProcess :
    Feature,
    Feature.CLI,
    Feature.CLI.Options,
    Feature.Out.Text.PrintStream {

    private lateinit var out: PrintStream
    private lateinit var commandLine: CommandLine

    override fun meta(context: FeatureContext) = Meta("print statistic about process")

    override fun run(context: FeatureContext) {
        val command = commandLine.getOptionValue(COMMAND)
        val stat = execProcess(command)
        val gson = GsonBuilder()
            .setPrettyPrinting()
            .create()
        gson.toJson(stat, out)
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

    override fun setCommandLine(commandLine: CommandLine) {
        this.commandLine = commandLine
    }

    override fun getOptions() =
        arrayOf(
            Option
                .builder("c")
                .longOpt(COMMAND)
                .hasArg(true)
                .argName("system command line")
                .required(true)
                .desc("e.g. 'dvh --help'")
                .build()
        )

    override fun setOut(out: PrintStream) {
        this.out = out
    }

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