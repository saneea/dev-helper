package io.github.saneea.feature.print;

import java.io.PrintStream;
import java.util.function.Consumer;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

import io.github.saneea.Feature;
import io.github.saneea.FeatureContext;

public class PrintLine implements //
		Feature, //
		Feature.CLI, //
		Feature.Out.Text.PrintStream //
{

	private static final String TEXT = "text";
	private static final String NO_NEW_LINE = "noNewLine";

	private PrintStream out;
	private CommandLine commandLine;

	@Override
	public String getShortDescription() {
		return "print text to output";
	}

	@Override
	public void run(FeatureContext context) throws Exception {

		String text = commandLine.getOptionValue(TEXT);

		Consumer<String> printFunc = //
				commandLine.hasOption(NO_NEW_LINE)//
						? out::print //
						: out::println;

		printFunc.accept(text);
	}

	@Override
	public void setOut(PrintStream out) {
		this.out = out;
	}

	@Override
	public Option[] getOptions() {
		return new Option[] { //

				Option//
						.builder("t")//
						.longOpt(TEXT)//
						.hasArg(true)//
						.argName("text for printing")//
						.required(true)//
						.desc("e.g. 'Hello World'")//
						.build(), //

				Option//
						.builder("nnl")//
						.longOpt(NO_NEW_LINE)//
						.required(false)//
						.desc("do not add new line at the end of output")//
						.build()//
		};
	}

	@Override
	public void setCommandLine(CommandLine commandLine) {
		this.commandLine = commandLine;
	}

}
