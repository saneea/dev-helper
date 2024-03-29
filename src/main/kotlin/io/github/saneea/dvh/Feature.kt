package io.github.saneea.dvh

import org.apache.commons.cli.CommandLine
import org.apache.commons.cli.Option
import java.io.InputStream
import java.io.OutputStream

typealias StringConsumer = (String) -> Unit

interface Feature {

    val meta: Meta

    fun run()

    data class Meta(
        val description: Description,
        val examples: List<Example> = emptyList()
    ) {
        constructor(shortDescription: String) : this(Description(shortDescription))

        data class Example(
            val name: String,
            val body: String,
            val result: String? = null
        )

        data class Description(
            val brief: String,
            val detailed: String = brief
        )
    }

    interface Out {
        interface Bin {
            interface Stream {
                var outBinStream: OutputStream
            }
        }

        interface Text {
            interface PrintStream {
                var outTextPrintStream: java.io.PrintStream
            }

            interface Writer {
                var outTextWriter: java.io.Writer
            }

            interface String {
                var outTextString: StringConsumer
            }
        }
    }

    interface In {
        interface Bin {
            interface Stream {
                var inBinStream: InputStream
            }
        }

        interface Text {
            interface Reader {
                var inTextReader: java.io.Reader
            }

            interface String {
                var inTextString: kotlin.String
            }
        }
    }

    interface Err {
        interface Bin {
            interface Stream {
                var errBinStream: OutputStream
            }
        }
    }

    interface CLI {
        var commandLine: CommandLine

        interface Options {
            val options: List<Option>
        }

        interface CommonOptions {
            companion object {
                const val HELP = "help"
                const val OUTPUT_ENCODING = "outputEncoding"
                const val INPUT_ENCODING = "inputEncoding"
                const val INPUT_FILE = "input"
                const val OUTPUT_FILE = "output"
                const val NON_BUFFERED_STREAMS = "nonBufferedStreams"

                val HELP_OPTION: Option = Option
                    .builder("h")
                    .longOpt(HELP)
                    .hasArg(false)
                    .required(false)
                    .desc("print help")
                    .build()

                val OUTPUT_ENCODING_OPTION: Option = Option
                    .builder("oe")
                    .longOpt(OUTPUT_ENCODING)
                    .hasArg(true)
                    .argName("encoding")
                    .required(false)
                    .desc("output encoding")
                    .build()

                val INPUT_ENCODING_OPTION: Option = Option
                    .builder("ie")
                    .longOpt(INPUT_ENCODING)
                    .hasArg(true)
                    .argName("encoding")
                    .required(false)
                    .desc("input encoding")
                    .build()

                val NON_BUFFERED_STREAMS_OPTION: Option = Option
                    .builder("nbs")
                    .longOpt(NON_BUFFERED_STREAMS)
                    .hasArg(false)
                    .required(false)
                    .desc("do not use memory buffers for i/o operations")
                    .build()

                val INPUT_FILE_OPTION: Option = Option
                    .builder("i")
                    .longOpt(INPUT_FILE)
                    .hasArg(true)
                    .argName("file name")
                    .required(false)
                    .desc("input file path")
                    .build()

                val OUTPUT_FILE_OPTION: Option = Option
                    .builder("o")
                    .longOpt(OUTPUT_FILE)
                    .hasArg(true)
                    .argName("file name")
                    .required(false)
                    .desc("output file path")
                    .build()
            }
        }
    }

    interface ContextAware {
        var context: FeatureContext
    }
}