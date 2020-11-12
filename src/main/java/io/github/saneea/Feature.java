package io.github.saneea;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

import io.github.saneea.Feature.Util.IOConsumer;

public interface Feature {

	Option[] EMPTY_OPTIONS_ARRAY = {};

	void run(FeatureContext context) throws Exception;

	String getShortDescription();

	default Option[] getOptions() {
		return EMPTY_OPTIONS_ARRAY;
	}

	interface Out {

		interface Bin {

			interface Stream {
				void setOut(OutputStream out);
			}

		}

		interface Text {

			interface PrintStream extends Bin.Stream {
				void setOut(java.io.PrintStream out);

				default void setOut(OutputStream out) {
				}
			}

			interface Writer extends Bin.Stream {
				void setOut(java.io.Writer out);

				default void setOut(OutputStream out) {
				}
			}

			interface String {
				void setOut(IOConsumer<java.lang.String> out);
			}

		}

	}

	interface Util {

		interface IOConsumer<T> {
			void accept(T obj) throws IOException;
		}
	}

	interface In {

		interface Bin {

			interface Stream {
				void setIn(InputStream in);
			}

		}

		interface Text {

			interface Reader extends Bin.Stream {
				void setIn(java.io.Reader in);

				default void setIn(InputStream in) {
				}
			}

			interface String {
				void setIn(java.lang.String in);
			}

		}

	}

	interface Err {

		interface Bin {

			interface Stream {
				void setErr(OutputStream err);
			}

		}

	}

	interface CLI {

		void setCommandLine(CommandLine commandLine);

		interface CommonOptions {

			String HELP = "help";
			String OUTPUT_ENCODING = "outputEncoding";
			String INPUT_ENCODING = "inputEncoding";
			String NON_BUFFERED_STREAMS = "nonBufferedStreams";

			Option HELP_OPTION = Option//
					.builder("h")//
					.longOpt(HELP)//
					.hasArg(false)//
					.required(false)//
					.desc("print help")//
					.build();

			Option OUTPUT_ENCODING_OPTION = Option//
					.builder("oe")//
					.longOpt(OUTPUT_ENCODING)//
					.hasArg(true)//
					.argName("encoding")//
					.required(false)//
					.desc("output encoding")//
					.build();

			Option INPUT_ENCODING_OPTION = Option//
					.builder("ie")//
					.longOpt(INPUT_ENCODING)//
					.hasArg(true)//
					.argName("encoding")//
					.required(false)//
					.desc("input encoding")//
					.build();

			Option NON_BUFFERED_STREAMS_OPTION = Option//
					.builder("nbs")//
					.longOpt(NON_BUFFERED_STREAMS)//
					.hasArg(false)//
					.required(false)//
					.desc("do not use memory buffers for i/o operations")//
					.build();
		}
	}
}
