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
				void setOut(OutputStream outputStreamOut);
			}

		}

		interface Text {

			interface PrintStream {
				void setOut(java.io.PrintStream printStreamOut);
			}

			interface Writer {
				void setOut(java.io.Writer out);
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
				void setIn(InputStream inputStream);
			}

		}

		interface Text {

			interface Reader {
				void setIn(java.io.Reader reader);
			}

			interface String {
				void setIn(java.lang.String in);
			}

		}

	}

	interface CLI {

		void setCommandLine(CommandLine commandLine);

		interface CommonOptions {

			String OUTPUT_ENCODING = "outputEncoding";
			String INPUT_ENCODING = "inputEncoding";

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
		}
	}
}
