package io.github.saneea.feature.multi;

import java.io.PrintStream;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import io.github.saneea.Feature;
import io.github.saneea.Feature.CLI.CommonOptions;
import io.github.saneea.FeatureContext;
import io.github.saneea.FeatureProvider;
import io.github.saneea.FeatureRunner;
import io.github.saneea.textfunction.Utils;
import io.github.saneea.textfunction.Utils.DefaultHelpPrinter;

public abstract class MultiFeature implements Feature {

	public static final String CATALOG = "catalog";
	public static final String CATALOG_LIST = "list";
	public static final String CATALOG_TREE = "tree";

	private PrintStream out = System.out;

	public abstract FeatureProvider getFeatureProvider();

	public void run(FeatureContext context) throws Exception {
		String[] args = context.getArgs();
		if (args.length == 0 || args[0].startsWith("-")) {
			runFeatureWithOptions(context, args);
		} else {
			runChildFeature(context, args);
		}
	}

	private void runChildFeature(FeatureContext context, String[] args) throws Exception {
		String featureName = args[0];
		String[] featureArgs = withoutFeatureName(args);

		FeatureRunner featureRunner = new FeatureRunner(getFeatureProvider());
		featureRunner.run(context, featureName, featureArgs);
	}

	private void runFeatureWithOptions(FeatureContext context, String[] args) {
		Options cliOptions = new Options()//
				.addOption(CommonOptions.HELP_OPTION)//
				.addOption(Option//
						.builder("c")//
						.longOpt(CATALOG)//
						.hasArg(true)//
						.argName(CATALOG_LIST + '|' + CATALOG_TREE)//
						.required(false)//
						.desc("features catalog show mode")//
						.build());

		FeatureContext childContext = new FeatureContext(//
				new FeatureContext.Parent(//
						context, getFeatureProvider()), //
				null, args);

		String featuresChain = childContext.getParent().getContext().getFeaturesChain()//
				.collect(//
						Collectors.joining(" "));

		MultiFeatureHelpPrinter helpPrinter = new MultiFeatureHelpPrinter(//
				featuresChain, cliOptions, childContext);

		CommandLine commandLine = Utils.parseCli(context.getFeatureName(), args, cliOptions, helpPrinter);

		helpPrinter.print(Optional.of(commandLine));
	}

	private class MultiFeatureHelpPrinter extends DefaultHelpPrinter {

		private final FeatureContext childContext;

		public MultiFeatureHelpPrinter(String cmdLineSyntax, Options options, FeatureContext childContext) {
			super(cmdLineSyntax, options);
			this.childContext = childContext;
		}

		@Override
		public void print(Optional<CommandLine> commandLine) {
			out.println("usage: "//
					+ cmdLineSyntax //
					+ " <featureName> [feature args]");
			out.println("--- or ---");

			super.print(commandLine);

			printFeaturesCatalog(childContext, commandLine);
		}

	}

	private static String[] withoutFeatureName(String[] args) {
		String[] newArgs;
		if (args.length == 0) {
			newArgs = args;
		} else {
			newArgs = new String[args.length - 1];
			System.arraycopy(args, 1, newArgs, 0, newArgs.length);
		}
		return newArgs;
	}

	private void printFeaturesCatalog(FeatureContext context, Optional<CommandLine> commandLine) {

		FeatureContext.Parent parent = context.getParent();

		FeatureProvider parentFeatureProvider = parent.getFeatureProvider();

		String catalogMode = commandLine//
				.map(cl -> cl.getOptionValue(CATALOG))//
				.map(Optional::ofNullable)//
				.flatMap(Function.identity())//
				.orElse(CATALOG_LIST);

		out.println();
		out.println("available features:");

		getCatalogPrinter(catalogMode)//
				.print(parentFeatureProvider, context);
	}

	private FeatureCatalogPrinter getCatalogPrinter(String catalogMode) {
		switch (catalogMode) {
		case CATALOG_LIST:
			return new ListPrinter(out);

		case CATALOG_TREE:
			return new TreePrinter(out);

		default:
			throw new IllegalArgumentException("Invalid " + CATALOG + " mode: " + catalogMode);
		}
	}

}
