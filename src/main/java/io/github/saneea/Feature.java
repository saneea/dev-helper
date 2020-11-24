package io.github.saneea;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

import io.github.saneea.Feature.Util.IOConsumer;

public interface Feature {

	void run(FeatureContext context) throws Exception;

	Meta meta(FeatureContext context);

	interface Meta {

		Description description();

		List<Example> examples();

		interface Example {
			String name();

			String body();

			static Example from(String name, String body) {
				return new Example() {
					@Override
					public String name() {
						return name;
					}

					@Override
					public String body() {
						return body;
					}
				};
			}
		}

		interface Description {
			String brief();

			String detailed();

			static Description from(String brief) {
				return from(brief, brief);
			}

			static Description from(String brief, String detailed) {
				return new Description() {
					@Override
					public String brief() {
						return brief;
					}

					@Override
					public String detailed() {
						return detailed;
					}
				};
			}
		}

		static Meta from(String shortDescription) {
			return from(Description.from(shortDescription));
		}

		static Meta from(Description description) {
			return from(description, Collections.emptyList());
		}

		static Meta from(Description description, List<Example> examples) {
			return new Meta() {
				@Override
				public Description description() {
					return description;
				}

				@Override
				public List<Example> examples() {
					return examples;
				}
			};
		}

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

		interface Options {
			Option[] getOptions();
		}

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
