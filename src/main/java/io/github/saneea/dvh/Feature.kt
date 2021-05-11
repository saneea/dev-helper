package io.github.saneea.dvh

import org.apache.commons.cli.CommandLine
import org.apache.commons.cli.Option
import java.io.InputStream
import java.io.OutputStream

typealias StringConsumer = (String) -> Unit

interface Feature {

    fun run(context: FeatureContext)

    fun meta(context: FeatureContext): Meta

    interface Meta {
        fun description(): Description
        fun examples(): List<Example>

        interface Example {
            fun name(): String
            fun body(): String
            fun result(): String?

            companion object {
                fun from(name: String, body: String, result: String? = null): Example {
                    return object : Example {
                        override fun name() = name
                        override fun body() = body
                        override fun result() = result
                    }
                }
            }
        }

        interface Description {
            fun brief(): String
            fun detailed(): String

            companion object {
                fun from(brief: String, detailed: String = brief): Description {
                    return object : Description {
                        override fun brief() = brief
                        override fun detailed() = detailed
                    }
                }
            }
        }

        companion object {
            fun from(shortDescription: String) = from(Description.from(shortDescription))

            fun from(description: Description, examples: List<Example> = emptyList()) =
                object : Meta {
                    override fun description() = description
                    override fun examples() = examples
                }
        }
    }

    interface Out {
        interface Bin {
            interface Stream {
                fun setOut(out: OutputStream)
            }
        }

        interface Text {
            interface PrintStream : Bin.Stream {
                fun setOut(out: java.io.PrintStream)
                override fun setOut(out: OutputStream) {}
            }

            interface Writer : Bin.Stream {
                fun setOut(out: java.io.Writer)
                override fun setOut(out: OutputStream) {}
            }

            interface String {
                fun setOut(out: StringConsumer)
            }
        }
    }

    interface In {
        interface Bin {
            interface Stream {
                fun setIn(`in`: InputStream)
            }
        }

        interface Text {
            interface Reader : Bin.Stream {
                fun setIn(`in`: java.io.Reader)
                override fun setIn(`in`: InputStream) {}
            }

            interface String {
                fun setIn(`in`: kotlin.String)
            }
        }
    }

    interface Err {
        interface Bin {
            interface Stream {
                fun setErr(err: OutputStream)
            }
        }
    }

    interface CLI {
        fun setCommandLine(commandLine: CommandLine)

        interface Options {
            fun getOptions(): Array<Option>
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