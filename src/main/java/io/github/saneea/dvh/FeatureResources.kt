package io.github.saneea.dvh

import io.github.saneea.dvh.Feature.CLI
import io.github.saneea.dvh.Feature.CLI.CommonOptions
import io.github.saneea.dvh.Feature.Util.IOConsumer
import io.github.saneea.dvh.utils.ByteSequenceRecognizer
import io.github.saneea.dvh.utils.EncodingRecognizer
import io.github.saneea.dvh.utils.Utils
import org.apache.commons.cli.CommandLine
import org.apache.commons.cli.Option
import org.apache.commons.cli.Options
import java.io.*
import java.nio.charset.Charset
import java.util.*
import java.util.stream.Stream

class FeatureResources(
    private val feature: Feature,
    private val args: Array<String>,
    private val context: FeatureContext
) : AutoCloseable {

    private val closeables: Deque<AutoCloseable?> = ArrayDeque()
    private var cliOptions: Options? = null

    var commandLine: CommandLine? = null
        get() {
            if (field == null) {
                field = Utils.parseCli(args, getCliOptions(), feature, context)
            }
            return field
        }
        private set

    var outTextPrintStream: PrintStream? = null
        get() {
            if (field == null) {
                field = PrintStream(
                    outBinStream,
                    false,
                    getEncoding(CommonOptions.OUTPUT_ENCODING)
                )
                closeables.add(field)
            }
            return field
        }
        private set

    var outTextWriter: Writer? = null
        get() {
            if (field == null) {
                field = OutputStreamWriter(
                    outBinStream,
                    getEncoding(CommonOptions.OUTPUT_ENCODING)
                )
                closeables.add(field)
            }
            return field
        }
        private set

    var outBinStream: OutputStream? = null
        get() {
            if (field == null) {
                field = FileOutputStream(FileDescriptor.out)
                if (useBufferedStreams()) {
                    field = BufferedOutputStream(field)
                }
                closeables.add(field)
            }
            return field
        }
        private set

    var errBinStream: OutputStream? = null
        get() {
            if (field == null) {
                field = FileOutputStream(FileDescriptor.err)
                closeables.add(field)
            }
            return field
        }
        private set

    @get:Throws(IOException::class)
    var inTextReader: Reader? = null
        get() {
            if (field == null) {
                field = Utils.skipBom(
                    InputStreamReader(
                        inBinStream,
                        inputEncoding
                    )
                )
                closeables.add(field)
            }
            return field
        }
        private set

    @get:Throws(IOException::class)
    var inTextString: String? = null
        get() {
            if (field == null) {
                field = readAll(inTextReader)
            }
            return field
        }
        private set
    private var inputEncodingRecognizer: ByteSequenceRecognizer<Charset>? = null

    @get:Throws(IOException::class)
    private var inputEncoding: Charset? = null
        private get() {
            if (field == null) {
                val encodingName = commandLine!!.getOptionValue(CommonOptions.INPUT_ENCODING)
                field = if (encodingName != null
                ) Charset.forName(encodingName)
                else encodingRecognizer
                    .result()
                    .orElseGet { Charset.defaultCharset() }
            }
            return field
        }

    fun getCliOptions(): Options {
        if (cliOptions == null) {
            cliOptions = Options()
            if (feature is CLI.Options) {
                Stream
                    .of(
                        *(feature as CLI.Options)
                            .options
                    )
                    .forEach { opt: Option? -> cliOptions!!.addOption(opt) }
            }
            cliOptions!!.addOption(CommonOptions.HELP_OPTION)
            if (feature is Feature.Out.Text.PrintStream
                || feature is Feature.Out.Text.Writer
                || feature is Feature.Out.Text.String
            ) {
                cliOptions!!.addOption(CommonOptions.OUTPUT_ENCODING_OPTION)
            }
            if (feature is Feature.In.Text.Reader
                || feature is Feature.In.Text.String
            ) {
                cliOptions!!.addOption(CommonOptions.INPUT_ENCODING_OPTION)
            }
            if (feature is Feature.In.Bin.Stream
                || feature is Feature.Out.Bin.Stream
            ) {
                cliOptions!!.addOption(CommonOptions.NON_BUFFERED_STREAMS_OPTION)
            }
        }
        return cliOptions!!
    }

    val outTextString: IOConsumer<String>
        get() = IOConsumer { str: String? -> outTextWriter!!.write(str) }

    private fun getEncoding(encodingOptionName: String): Charset {
        val encodingName = commandLine!!.getOptionValue(encodingOptionName)
        return if (encodingName != null
        ) Charset.forName(encodingName)
        else Charset.defaultCharset()
    }

    @get:Throws(IOException::class)
    val inBinStream: InputStream
        get() = encodingRecognizer!!.stream()

    @get:Throws(IOException::class)
    val encodingRecognizer: ByteSequenceRecognizer<Charset>
        get() {
            if (inputEncodingRecognizer == null) {
                inputEncodingRecognizer = EncodingRecognizer.recognize(createInternalInBinStream())
                closeables.add(inputEncodingRecognizer)
            }
            return inputEncodingRecognizer!!
        }

    private fun createInternalInBinStream(): InputStream {
        var inBinStream: InputStream = FileInputStream(FileDescriptor.`in`)
        if (useBufferedStreams()) {
            inBinStream = BufferedInputStream(inBinStream)
        }
        return inBinStream
    }

    private fun useBufferedStreams(): Boolean {
        return !commandLine!!.hasOption(CommonOptions.NON_BUFFERED_STREAMS)
    }

    @Throws(Exception::class)
    override fun close() {
        var onCloseException: Exception? = null
        while (!closeables.isEmpty()) {
            val closeable = closeables.pollLast()
            try {
                closeable!!.close()
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

    companion object {
        @Throws(IOException::class)
        private fun readAll(reader: Reader?): String {
            val buf = StringWriter()
            reader!!.transferTo(buf)
            return buf.toString()
        }
    }
}