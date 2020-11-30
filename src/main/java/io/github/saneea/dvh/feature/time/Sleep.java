package io.github.saneea.dvh.feature.time;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.PatternOptionBuilder;

import io.github.saneea.dvh.Feature;
import io.github.saneea.dvh.FeatureContext;

public class Sleep implements//
		Feature, //
		Feature.CLI, //
		Feature.CLI.Options {

	private static final String DURATION = "duration";
	private static final String DURATION_SHORT = "d";
	private static final String UNITS = "units";
	private static final String UNITS_SHORT = "u";

	private enum Units {
		MS("ms", "millisecond", TimeUnit.MILLISECONDS), //
		S("s", "second", TimeUnit.SECONDS), //
		M("m", "minute", TimeUnit.MINUTES), //
		H("h", "hour", TimeUnit.HOURS), //
		D("d", "day", TimeUnit.DAYS);

		public static final Units DEFAULT = S;

		public final String cli;
		public final String display;
		public final TimeUnit javaTimeUnit;

		Units(String cli, String display, TimeUnit javaTimeUnit) {
			this.cli = cli;
			this.display = display;
			this.javaTimeUnit = javaTimeUnit;
		}

		public static Stream<Units> stream() {
			return Stream.of(Units.values());
		}

		private static Units fromCli(String optValue) {
			return stream()//
					.filter(unit -> unit.cli.equals(optValue))//
					.findAny()//
					.orElseThrow(() -> new IllegalArgumentException("Illegal units type: " + optValue));
		}
	}

	private CommandLine commandLine;

	@Override
	public Meta meta(FeatureContext context) {
		String execWithD = context.getFeaturesChainString() + " -" + DURATION_SHORT;
		return Meta.from(//
				Meta.Description.from(//
						"wait some time before exit"), //
				Arrays.asList(//
						Meta.Example.from(//
								"wait 5 seconds", //
								execWithD + " 5"), //
						Meta.Example.from(//
								"wait 5000 milliseconds (5 seconds)", //
								execWithD + " 5000" + " -" + UNITS_SHORT + " " + Units.MS.cli)//
				));
	}

	@Override
	public void run(FeatureContext context) throws InterruptedException {
		String timeUnitStr = commandLine.getOptionValue(UNITS, Units.DEFAULT.cli);
		TimeUnit timeUnit = Units.fromCli(timeUnitStr).javaTimeUnit;
		String durationStr = commandLine.getOptionValue(DURATION);
		long duration = Long.parseLong(durationStr);

		timeUnit.sleep(duration);
	}

	@Override
	public Option[] getOptions() {
		Option[] options = { //
				Option//
						.builder(DURATION_SHORT)//
						.longOpt(DURATION)//
						.hasArg(true)//
						.argName("duration")//
						.required(true)//
						.desc("time duration")//
						.type(PatternOptionBuilder.NUMBER_VALUE)//
						.build(), //
				Option//
						.builder(UNITS_SHORT)//
						.longOpt(UNITS)//
						.hasArg(true)//
						.argName(Units.stream()//
								.map(u -> u.cli)//
								.collect(Collectors.joining("|")))//
						.required(false)//
						.desc("time units, default is '" + Units.DEFAULT.cli + "' (" + //
								Units.stream()//
										.map(u -> u.cli + " - " + u.display)//
										.collect(Collectors.joining(", "))//
								+ ")")//
						.build()//
		};

		return options;
	}

	@Override
	public void setCommandLine(CommandLine commandLine) {
		this.commandLine = commandLine;
	}

}
