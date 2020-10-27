package io.github.saneea.feature.process;

import java.io.IOException;
import java.io.PrintStream;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import io.github.saneea.Feature;
import io.github.saneea.FeatureContext;

public class InspectProcess implements//
		Feature, //
		Feature.CLI, //
		Feature.Out.Text.PrintStream//
{

	private static final String COMMAND = "command";

	private PrintStream out;

	private CommandLine commandLine;

	@Override
	public String getShortDescription() {
		return "print statistic about process";
	}

	@Override
	public void run(FeatureContext context) throws Exception {

		String command = commandLine.getOptionValue(COMMAND);

		ProcessExecutionInfo stat = execProcess(command);

		Gson gson = new GsonBuilder()//
				.setPrettyPrinting()//
				.create();

		gson.toJson(stat, out);
	}

	private ProcessExecutionInfo execProcess(String command) throws IOException, InterruptedException {

		ProcessExecutionInfo stat = new ProcessExecutionInfo();

		stat.startTime = System.currentTimeMillis();

		Process forkProc = Runtime.getRuntime().exec(command);

		stat.exitCode = forkProc.waitFor();

		stat.finishTime = System.currentTimeMillis();

		stat.duration = stat.finishTime - stat.startTime;

		return stat;
	}

	@Override
	public void setCommandLine(CommandLine commandLine) {
		this.commandLine = commandLine;
	}

	@Override
	public Option[] getOptions() {
		return new Option[] { //

				Option//
						.builder("c")//
						.longOpt(COMMAND)//
						.hasArg(true)//
						.argName("system command line")//
						.required(true)//
						.desc("e.g. 'dvh.bat help'")//
						.build()//
		};
	}

	@Override
	public void setOut(PrintStream out) {
		this.out = out;
	}

	public static class ProcessExecutionInfo {
		public int exitCode;
		public long startTime;
		public long finishTime;
		public long duration;
	}

}
