package io.github.saneea.dvh.feature.xml;

import io.github.saneea.dvh.Feature;
import io.github.saneea.dvh.FeatureContext;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class FilteredComparison implements//
		Feature, //
		Feature.CLI, //
		Feature.CLI.Options {

	private CommandLine commandLine;

	@Override
	public Meta meta(FeatureContext context) {
		return Meta.from("run external comparison tool for 'pretty printed' xml files");
	}

	@Override
	public void run(FeatureContext context) throws Exception {
		String comparisonTool = commandLine.getOptionValue(Params.COMPARISON_TOOL);
		String leftFileName = commandLine.getOptionValue(Params.LEFT);
		String rightFileName = commandLine.getOptionValue(Params.RIGHT);

		String leftFilteredFileName;
		String rightFilteredFileName;
		try {
			leftFilteredFileName = getFilteredFile(leftFileName);
			rightFilteredFileName = getFilteredFile(rightFileName);
		} catch (Exception e) {
			leftFilteredFileName = leftFileName;
			rightFilteredFileName = rightFileName;
		}

		ProcessBuilder processBuilder = new ProcessBuilder(comparisonTool, leftFilteredFileName, rightFilteredFileName);
		Process process = processBuilder.start();
		process.waitFor();
	}

	private static String getFilteredFile(String inputFileName) throws Exception {
		File outputFile = File.createTempFile("filtered", null);
		outputFile.deleteOnExit();
		String outputFileName = outputFile.getPath();

		try (//
				Reader in = new InputStreamReader(//
						new BufferedInputStream(//
								new FileInputStream(//
										inputFileName)), //
						StandardCharsets.UTF_8); //

				Writer out = new OutputStreamWriter(//
						new BufferedOutputStream(//
								new FileOutputStream(//
										outputFile)), //
						StandardCharsets.UTF_8);//
		) {
			XmlPrettyPrint.Companion.run(in, out, false);
		}
		return outputFileName;
	}

	public static class Params {

		public static String COMPARISON_TOOL = "comparisonTool";
		public static String LEFT = "left";
		public static String RIGHT = "right";

		private static Option[] createOptions() {
			Option[] options = {

					Option//
							.builder("ct")//
							.longOpt(COMPARISON_TOOL)//
							.hasArg(true)//
							.argName("system command")//
							.required(true)//
							.desc("system command for comparison of just path to extrenal comparison tool")//
							.build(), //
					Option//
							.builder("l")//
							.longOpt(LEFT)//
							.hasArg(true)//
							.argName("file path")//
							.required(true)//
							.desc("left side file for comparison")//
							.build(), //
					Option//
							.builder("r")//
							.longOpt(RIGHT)//
							.hasArg(true)//
							.argName("file path")//
							.required(true)//
							.desc("right side file for comparison")//
							.build()//
			};

			return options;
		}
	}

	@Override
	public void setCommandLine(CommandLine commandLine) {
		this.commandLine = commandLine;
	}

	@Override
	public Option[] getOptions() {
		return Params.createOptions();
	}

}
