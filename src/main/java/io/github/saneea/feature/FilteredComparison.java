package io.github.saneea.feature;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import io.github.saneea.Feature;

public class FilteredComparison implements Feature {

	@Override
	public void run(InputStream input, OutputStream output, String[] args) throws Exception {
		Options options = Params.createOptions();

		CommandLineParser commandLineParser = new DefaultParser();
		CommandLine commandLine = commandLineParser.parse(options, args);

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
		String outputFileName = outputFile.getName();
		XmlReform.execute(inputFileName, outputFileName, StandardCharsets.UTF_8.toString());
		return outputFileName;
	}

	public static class Params {

		public static String COMPARISON_TOOL = "comparisonTool";
		public static String LEFT = "left";
		public static String RIGHT = "right";

		private static Options createOptions() {
			Options options = new Options()//
					.addOption(Option//
							.builder("ct")//
							.longOpt(COMPARISON_TOOL)//
							.hasArg(true)//
							.argName("system command")//
							.required(true)//
							.desc("system command for comparison of just path to extrenal comparison tool")//
							.build())//
					.addOption(Option//
							.builder("l")//
							.longOpt(LEFT)//
							.hasArg(true)//
							.argName("file path")//
							.required(true)//
							.desc("left side file for comparison")//
							.build())//
					.addOption(Option//
							.builder("r")//
							.longOpt(RIGHT)//
							.hasArg(true)//
							.argName("file path")//
							.required(true)//
							.desc("right side file for comparison")//
							.build());

			return options;
		}
	}
}
