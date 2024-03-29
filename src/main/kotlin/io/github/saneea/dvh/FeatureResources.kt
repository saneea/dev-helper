package io.github.saneea.dvh

import io.github.saneea.dvh.Feature.CLI
import io.github.saneea.dvh.Feature.CLI.CommonOptions
import io.github.saneea.dvh.utils.ByteSequenceRecognizer
import io.github.saneea.dvh.utils.Utils
import io.github.saneea.dvh.utils.databuffer.WriteOnCloseOutputStream
import io.github.saneea.dvh.utils.encodingRecognizer
import org.apache.commons.cli.CommandLine
import org.apache.commons.cli.Options
import java.io.*
import java.nio.charset.Charset
import java.util.*

class FeatureResources(
    private val feature: Feature,
    private val context: FeatureContext
) : AutoCloseable {

    private val closeables: Deque<AutoCloseable> = ArrayDeque()
    private val exceptionListeners: MutableSet<(Exception) -> Unit> = LinkedHashSet()

    val commandLine: CommandLine by lazy {
        Utils.parseCli(cliOptions, feature, context)
    }

    val outTextPrintStream: PrintStream by lazy {
        PrintStream(outBinStream, false, getOutputEncoding())
            .also(closeables::add)
    }

    val outTextWriter: Writer by lazy {
        OutputStreamWriter(outBinStream, getOutputEncoding())
            .also(closeables::add)
    }

    val outBinStream: OutputStream by lazy {
        createInternalOutBinStream()
            .also(closeables::add)
    }

    private fun createInternalOutBinStream(): OutputStream {
        val outputFilePath = commandLine.getOptionValue(CommonOptions.OUTPUT_FILE)
        return if (outputFilePath != null)
            WriteOnCloseOutputStream({ FileOutputStream(outputFilePath) })
                .also { exceptionListeners.add { _ -> it.cancel() } }
        else
            FileOutputStream(FileDescriptor.out).let {
                if (useBufferedStreams())
                    BufferedOutputStream(it)
                else
                    it
            }
    }

    val errBinStream: OutputStream by lazy {
        FileOutputStream(FileDescriptor.err)
            .also(closeables::add)
    }

    val inTextReader: Reader by lazy {
        Utils.skipBom(InputStreamReader(inBinStream, inputEncoding))
            .also(closeables::add)
    }

    val inTextString: String by lazy {
        inTextReader.readText()
    }

    private val inputEncoding: Charset by lazy {
        commandLine.getOptionValue(CommonOptions.INPUT_ENCODING)
            ?.let(Charset::forName)
            ?: encodingRecognizer.result
            ?: Charset.defaultCharset()
    }

    private val cliOptions: Options by lazy {
        Options().also {

            if (feature is CLI.Options) {
                feature.options.forEach(it::addOption)
            }

            it.addOption(CommonOptions.HELP_OPTION)

            when (feature) {
                is Feature.Out.Text.PrintStream,
                is Feature.Out.Text.Writer,
                is Feature.Out.Text.String ->
                    it.addOption(CommonOptions.OUTPUT_ENCODING_OPTION)
            }

            when (feature) {
                is Feature.In.Text.Reader,
                is Feature.In.Text.String ->
                    it.addOption(CommonOptions.INPUT_ENCODING_OPTION)
            }

            when (feature) {
                is Feature.In.Bin.Stream,
                is Feature.In.Text.Reader,
                is Feature.Out.Bin.Stream,
                is Feature.Out.Text.PrintStream,
                is Feature.Out.Text.Writer ->
                    it.addOption(CommonOptions.NON_BUFFERED_STREAMS_OPTION)
            }

            when (feature) {
                is Feature.In.Bin.Stream,
                is Feature.In.Text.Reader,
                is Feature.In.Text.String ->
                    it.addOption(CommonOptions.INPUT_FILE_OPTION)
            }

            when (feature) {
                is Feature.Out.Bin.Stream,
                is Feature.Out.Text.Writer,
                is Feature.Out.Text.PrintStream,
                is Feature.Out.Text.String ->
                    it.addOption(CommonOptions.OUTPUT_FILE_OPTION)
            }
        }
    }

    val outTextString: StringConsumer by lazy {
        { str: String -> outTextWriter.write(str) }
    }

    private fun getOutputEncoding() = commandLine
        .getOptionValue(CommonOptions.OUTPUT_ENCODING)
        ?.let(Charset::forName)
        ?: Charset.defaultCharset()

    val inBinStream: InputStream by lazy {
        encodingRecognizer.stream
    }

    private val encodingRecognizer: ByteSequenceRecognizer<Charset> by lazy {
        encodingRecognizer(createInternalInBinStream())
            .also(closeables::add)
    }

    private fun createInternalInBinStream(): InputStream {
        val inputFilePath = commandLine.getOptionValue(CommonOptions.INPUT_FILE)
        return if (inputFilePath != null)
            FileInputStream(inputFilePath)
        else
            FileInputStream(FileDescriptor.`in`).let {
                if (useBufferedStreams())
                    BufferedInputStream(it)
                else
                    it
            }
    }

    private fun useBufferedStreams() = !commandLine.hasOption(CommonOptions.NON_BUFFERED_STREAMS)

    override fun close() {
        var onCloseException: Exception? = null
        while (!closeables.isEmpty()) {
            val closeable = closeables.pollLast()
            try {
                closeable.close()
            } catch (e: Exception) {
                if (onCloseException != null) {
                    e.addSuppressed(onCloseException)
                }
                onCloseException = e
            }
        }
        if (onCloseException != null) {
            throw onCloseException
        }
    }

    fun onException(e: Exception) = exceptionListeners.forEach { it.invoke(e) }
}
