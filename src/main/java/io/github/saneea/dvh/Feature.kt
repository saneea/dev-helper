package io.github.saneea.dvh

import org.apache.commons.cli.CommandLine
import org.apache.commons.cli.Option
import java.io.InputStream
import java.io.OutputStream

typealias StringConsumer = (String) -> Unit

interface Feature {

    fun run(context: FeatureContext)

    fun meta(context: FeatureContext): Meta

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
                fun setOutBinStream(out: OutputStream)
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
                fun setInBinStream(`in`: InputStream)
            }
        }

        interface Text {
            interface Reader {
                fun setInTextReader(`in`: java.io.Reader)
            }

            interface String {
                fun setInTextString(`in`: kotlin.String)
            }
        }
    }

    interface Err {
        interface Bin {
            interface Stream {
                fun setErrBinStream(err: OutputStream)
            }
        }
    }

    interface CLI {
        fun setCommandLine(commandLine: CommandLine)

        interface Options {
            val options: Array<Option>
        }

        interface CommonOptions {
            companion object {
                const val HELP = "help"
                const val OUTPUT_ENCODING = "outputEncoding"
                const val INPUT_ENCODING = "inputEncoding"
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
            }
        }
    }
}