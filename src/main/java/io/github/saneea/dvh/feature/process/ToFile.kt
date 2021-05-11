package io.github.saneea.dvh.feature.process

import io.github.saneea.dvh.Feature
import io.github.saneea.dvh.Feature.Meta
import io.github.saneea.dvh.FeatureContext
import org.apache.commons.cli.CommandLine
import org.apache.commons.cli.Option
import java.io.*
import java.util.concurrent.Callable
import java.util.concurrent.Executors
import java.util.concurrent.Future
import kotlin.system.exitProcess

class ToFile :
    Feature,
    Feature.CLI,
    Feature.CLI.Options,
    Feature.Err.Bin.Stream {

    internal interface ExitCode {
        companion object {
            const val OK = 0
        }
    }

    private lateinit var err: OutputStream
    private lateinit var commandLine: CommandLine

    override fun meta(context: FeatureContext) =
        Meta.from("save output of external process to file")

    override fun run(context: FeatureContext) {
        val outFileName = commandLine.getOptionValue(OUTPUT)
        val command = commandLine.getOptionValue(COMMAND)
        val forkProc = Runtime.getRuntime().exec(command)
        getStdOutFromProc(forkProc, err)
            .use { stdOutBuffer ->
                val exitCode = forkProc.waitFor()
                if (exitCode == ExitCode.OK) {
                    bufferToFile(stdOutBuffer, outFileName)
                } else {
                    exitProcess(exitCode)
                }
            }
    }

    private fun bufferToFile(buffer: ByteArrayBuffer, fileName: String) {
        File(fileName)
            .outputStream().buffered()
            .use(buffer::writeTo)
    }

    private fun getStdOutFromProc(forkProc: Process, err: OutputStream): ByteArrayBuffer {
        forkProc.inputStream.buffered().use { forkProcOut ->
            forkProc.errorStream.buffered().use { forkProcErr ->
                val executorService = Executors.newFixedThreadPool(2)
                return try {
                    val transferStdOut = executorService.submit(transferToBuffer(forkProcOut))
                    val transferStdErr = executorService.submit(transfer(forkProcErr, err))
                    waitFuture(transferStdErr)
                    ByteArrayBuffer.of(transferStdOut.get())
                } finally {
                    executorService.shutdown()
                }
            }
        }
    }

    private fun waitFuture(future: Future<*>) {
        future.get()
    }

    private interface ByteArrayBuffer : Closeable {
        fun writeTo(out: OutputStream?)

        companion object {
            fun of(byteArrayOutputStream: ByteArrayOutputStream): ByteArrayBuffer {
                return object : ByteArrayBuffer {

                    override fun writeTo(out: OutputStream?) {
                        byteArrayOutputStream.writeTo(out)
                    }

                    override fun close() {
                        byteArrayOutputStream.close()
                    }
                }
            }
        }
    }

    override fun setErr(err: OutputStream) {
        this.err = err
    }

    override fun setCommandLine(commandLine: CommandLine) {
        this.commandLine = commandLine
    }

    override fun getOptions(): Array<Option> =
        arrayOf(
            Option
                .builder("c")
                .longOpt(COMMAND)
                .hasArg(true)
                .argName("system command")
                .required(true)
                .desc("system command for new process")
                .build(),
            Option
                .builder("o")
                .longOpt(OUTPUT)
                .hasArg(true)
                .argName("file name")
                .required(true)
                .desc("output file name")
                .build()
        )

    companion object {
        private const val OUTPUT = "output"
        private const val COMMAND = "command"

        private fun transferToBuffer(`in`: InputStream): Callable<ByteArrayOutputStream> =
            transfer(`in`, ByteArrayOutputStream())

        private fun <OutputStreamType : OutputStream> transfer(
            `in`: InputStream,
            out: OutputStreamType
        ): Callable<OutputStreamType> = Callable {
            `in`.transferTo(out)
            out
        }
    }
}