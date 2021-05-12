package io.github.saneea.dvh

import io.github.saneea.dvh.Feature.CLI
import io.github.saneea.dvh.Feature.CLI.CommonOptions
import io.github.saneea.dvh.utils.ByteSequenceRecognizer
import io.github.saneea.dvh.utils.Utils
import io.github.saneea.dvh.utils.encodingRecognizer
import org.apache.commons.cli.CommandLine
import org.apache.commons.cli.Options
import java.io.*
import java.nio.charset.Charset
import java.util.*

class FeatureResources(
    private val feature: Feature,
    private val args: Array<String>,
    private val context: FeatureContext
) : AutoCloseable {

    private val closeables: Deque<AutoCloseable> = ArrayDeque()

    val commandLine: CommandLine by lazy {
        Utils.parseCli(args, cliOptions, feature, context)
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
        var stream: OutputStream = FileOutputStream(FileDescriptor.out)
        if (useBufferedStreams()) {
            stream = BufferedOutputStream(stream)
        }
        closeables.add(stream)
        stream
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
            ?: encodingRecognizer.result()
            ?: Charset.defaultCharset()
    }

    private val cliOptions: Options by lazy {
        Options().also {

            if (feature is CLI.Options) {
                feature.getOptions().forEach(it::addOption)
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
                is Feature.Out.Bin.Stream ->
                    it.addOption(CommonOptions.NON_BUFFERED_STREAMS_OPTION)
            }
        }
    }

    val outTextString: StringConsumer
        get() = outTextWriter::write

    private fun getOutputEncoding() = commandLine
        .getOptionValue(CommonOptions.OUTPUT_ENCODING)
        ?.let(Charset::forName)
        ?: Charset.defaultCharset()

    val inBinStream: InputStream
        get() = encodingRecognizer.stream()

    private val encodingRecognizer: ByteSequenceRecognizer<Charset> by lazy {
        encodingRecognizer(createInternalInBinStream())
            .also(closeables::add)
    }

    private fun createInternalInBinStream(): InputStream {
        var inBinStream: InputStream = FileInputStream(FileDescriptor.`in`)
        if (useBufferedStreams()) {
            inBinStream = BufferedInputStream(inBinStream)
        }
        return inBinStream
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
}
