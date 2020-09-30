package io.github.saneea;

import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

public interface Feature {

	void run(FeatureContext context) throws Exception;

	String getShortDescription();

	interface Out {

		interface Bin {

			interface Stream {
				void setOutputStreamOut(OutputStream outputStreamOut);
			}

		}

		interface Text {

			interface PrintStream extends CLI {
				void setPrintStreamOut(java.io.PrintStream printStreamOut);
			}

			interface Writer extends CLI {
				void setWriter(java.io.Writer out);
			}

		}

	}

	interface In {

		interface Bin {

			interface Stream {
				void setInputStream(InputStream inputStream);
			}

		}

		interface Text {

			interface Reader extends CLI {
				void setReader(java.io.Reader reader);
			}

			interface String extends CLI {
				void setString(java.lang.String in);
			}

		}

	}

	interface CLI {

		Option[] EMPTY_OPTIONS_ARRAY = {};

		default Option[] getOptions() {
			return EMPTY_OPTIONS_ARRAY;
		}

		default void setCommandLine(CommandLine commandLine) {
		}

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
