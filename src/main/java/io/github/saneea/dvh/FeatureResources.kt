package io.github.saneea.dvh

import io.github.saneea.dvh.Feature.CLI
import io.github.saneea.dvh.Feature.CLI.CommonOptions
import io.github.saneea.dvh.utils.ByteSequenceRecognizer
import io.github.saneea.dvh.utils.Utils
import io.github.saneea.dvh.utils.encodingRecognizer
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

    //TODO many fields should be refactored as "lazy" properties

    private val closeables: Deque<AutoCloseable?> = ArrayDeque()
    private var cliOptions: Options? = null

    private var _commandLine: CommandLine? = null
    var commandLine: CommandLine
        get() {
            if (_commandLine == null) {
                _commandLine = Utils.parseCli(args, getCliOptions(), feature, context)
            }
            return _commandLine!!
        }
        private set(value) {
            _commandLine = value
        }

    private var _outTextPrintStream: PrintStream? = null
    var outTextPrintStream: PrintStream
        get() {
            if (_outTextPrintStream == null) {
                _outTextPrintStream = PrintStream(
                    outBinStream,
                    false,
                    getOutputEncoding()
                )
                closeables.add(_outTextPrintStream)
            }
            return _outTextPrintStream!!
        }
        private set(value) {
            _outTextPrintStream = value
        }

    private var _outTextWriter: Writer? = null
    var outTextWriter: Writer
        get() {
            if (_outTextWriter == null) {
                _outTextWriter = OutputStreamWriter(
                    outBinStream,
                    getOutputEncoding()
                )
                closeables.add(_outTextWriter)
            }
            return _outTextWriter!!
        }
        private set(value) {
            _outTextWriter = value
        }

    private var _outBinStream: OutputStream? = null
    var outBinStream: OutputStream
        get() {
            if (_outBinStream == null) {
                _outBinStream = FileOutputStream(FileDescriptor.out)
                if (useBufferedStreams()) {
                    _outBinStream = BufferedOutputStream(_outBinStream!!)
                }
                closeables.add(_outBinStream)
            }
            return _outBinStream!!
        }
        private set(value) {
            _outBinStream = value
        }

    private var _errBinStream: OutputStream? = null
    var errBinStream: OutputStream
        get() {
            if (_errBinStream == null) {
                _errBinStream = FileOutputStream(FileDescriptor.err)
                closeables.add(_errBinStream)
            }
            return _errBinStream!!
        }
        private set(value) {
            _errBinStream = value
        }

    private var _inTextReader: Reader? = null
    var inTextReader: Reader
        get() {
            if (_inTextReader == null) {
                _inTextReader = Utils.skipBom(
                    InputStreamReader(
                        inBinStream,
                        inputEncoding!!
                    )
                )
                closeables.add(_inTextReader)
            }
            return _inTextReader!!
        }
        private set(value) {
            _inTextReader = value
        }

    private var _inTextString: String? = null
    var inTextString: String
        get() {
            if (_inTextString == null) {
                _inTextString = inTextReader.readText()
            }
            return _inTextString!!
        }
        private set(value) {
            _inTextString = value
        }

    private var inputEncodingRecognizer: ByteSequenceRecognizer<Charset>? = null

    private var inputEncoding: Charset? = null
        get() {
            if (field == null) {
                val encodingName = commandLine.getOptionValue(CommonOptions.INPUT_ENCODING)
                field = if (encodingName != null
                ) Charset.forName(encodingName)
                else encodingRecognizer
                    .result()
                    .orElseGet { Charset.defaultCharset() }
            }
            return field
        }

    private fun getCliOptions(): Options {
        if (cliOptions == null) {
            cliOptions = Options()
            if (feature is CLI.Options) {
                Stream
                    .of(
                        *(feature as CLI.Options)
                            .getOptions()
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

    val outTextString: StringConsumer
        get() = outTextWriter::write

    private fun getOutputEncoding() = commandLine
        .getOptionValue(CommonOptions.OUTPUT_ENCODING)
        ?.let(Charset::forName)
        ?: Charset.defaultCharset()

    val inBinStream: InputStream
        get() = encodingRecognizer.stream()

    private val encodingRecognizer: ByteSequenceRecognizer<Charset>
        get() {
            if (inputEncodingRecognizer == null) {
                inputEncodingRecognizer = encodingRecognizer(createInternalInBinStream())
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

    private fun useBufferedStreams() = !commandLine.hasOption(CommonOptions.NON_BUFFERED_STREAMS)

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
}
