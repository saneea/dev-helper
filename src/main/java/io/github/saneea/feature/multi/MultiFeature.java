package io.github.saneea.feature.multi;

import java.io.PrintStream;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import io.github.saneea.Feature;
import io.github.saneea.Feature.CLI.CommonOptions;
import io.github.saneea.FeatureContext;
import io.github.saneea.FeatureContext.Parent;
import io.github.saneea.FeatureProvider;
import io.github.saneea.FeatureRunner;
import io.github.saneea.feature.help.FeatureTree;
import io.github.saneea.textfunction.Utils;
import io.github.saneea.textfunction.Utils.DefaultHelpPrinter;

public abstract class MultiFeature implements Feature {

	public static final String CATALOG = "catalog";
	public static final String CATALOG_LIST = "list";
	public static final String CATALOG_TREE = "tree";
	public static final String CATALOG_LINE_SUFFIX = " ";

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

		String featuresChain = getFeaturesChain(childContext.getParent().getContext())//
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

		switch (catalogMode) {

		case CATALOG_LIST:
			printCatalogAsList(parentFeatureProvider);
			break;

		case CATALOG_TREE:
			printCatalogAsTree(parentFeatureProvider, context);
			break;

		default:
			throw new IllegalArgumentException("Invalid " + CATALOG + " mode: " + catalogMode);
		}
	}

	private void printCatalogAsList(FeatureProvider parentFeatureProvider) {
		Set<String> featuresNames = parentFeatureProvider.featuresNames();

		int maxFeatureNameSize = Utils.getMaxStringLength(featuresNames);

		for (String featureName : featuresNames) {
			FeatureInfo featureInfo = getFeatureInfo(parentFeatureProvider, featureName);
			String template = CATALOG_LINE_SUFFIX + "%1$" + maxFeatureNameSize + "s - %2$s";

			out.println(String.format(template, featureName, featureInfo.description));
		}
	}

	private void printCatalogAsTree(FeatureProvider parentFeatureProvider, FeatureContext context) {

		List<FeatureTree> featuresChain = getFeaturesChain(context.getParent().getContext())//
				.map(featureName -> new FeatureTree(featureName, ""))//
				.collect(Collectors.toList());

		for (int i = 0; i < featuresChain.size() - 1; ++i) {
			featuresChain.get(i).getChildren().add(featuresChain.get(i + 1));
		}

		buildFeatureTree(featuresChain.get(featuresChain.size() - 1), parentFeatureProvider);

		printCatalogChildBranch("", featuresChain.get(0), 0, true, 0);
	}

	private void buildFeatureTree(FeatureTree parent, FeatureProvider featureProvider) {
		Set<String> featuresNames = featureProvider.featuresNames();

		for (String featureName : featuresNames) {
			FeatureInfo featureInfo = getFeatureInfo(featureProvider, featureName);

			FeatureTree child = new FeatureTree(featureName, featureInfo.description);
			parent.getChildren().add(child);

			if (featureInfo.feature instanceof MultiFeature) {
				MultiFeature multiFeature = (MultiFeature) featureInfo.feature;
				buildFeatureTree(child, multiFeature.getFeatureProvider());
			}
		}
	}

	private static FeatureInfo getFeatureInfo(FeatureProvider featureProvider, String featureName) {
		try {
			Feature feature = featureProvider.createFeature(featureName);
			return new FeatureInfo(feature, feature.getShortDescription());
		} catch (Exception e) {
			return new FeatureInfo(null, e.toString());
		}
	}

	private void printCatalogTreeBranch(FeatureTree featureTree, String levelLineSuffix, int level) {

		List<FeatureTree> features = featureTree.getChildren();

		int maxFeatureNameSize = Utils.getMaxStringLength(//
				features//
						.stream()//
						.map(FeatureTree::getAlias));

		for (int i = 0; i < features.size(); ++i) {
			printCatalogChildBranch(levelLineSuffix, features.get(i), maxFeatureNameSize, i == features.size() - 1,
					level + 1);
		}
	}

	private void printCatalogChildBranch(String levelLineSuffix, FeatureTree feature, int maxFeatureNameSize,
			boolean last, int level) {
		String featureName = feature.getAlias();
		String featureShortDescription = feature.getDescription();

		boolean isRoot = level == 0;

		boolean isHub = !feature.getChildren().isEmpty();

		StringBuilder featureLine = new StringBuilder();
		featureLine.append(levelLineSuffix);
		if (!isRoot) {
			featureLine.append(last ? "\\" : "+");
			featureLine.append("---");
		}
		featureLine.append(featureName);

		if (!isHub) {
			featureLine//
					.append(Utils.repeatString(" ", maxFeatureNameSize - featureName.length()))//
					.append(" - ")//
					.append(featureShortDescription);
		}

		out.println(featureLine);

		if (isHub) {
			StringBuilder nextLevelLineSuffix = new StringBuilder(levelLineSuffix);
			if (!isRoot) {
				nextLevelLineSuffix.append(last ? " " : "|");
				nextLevelLineSuffix.append("   ");
			}
			printCatalogTreeBranch(feature, nextLevelLineSuffix.toString(), level);
		} else if (last) {
			out.println(levelLineSuffix);
		}
	}

	private static Stream<String> getFeaturesChain(FeatureContext context) {
		if (context == null) {
			return Stream.empty();
		}

		Parent parent = context.getParent();
		FeatureContext parentContext = parent != null//
				? parent.getContext()//
				: null;

		return Stream.concat(//
				getFeaturesChain(parentContext), //
				Stream.of(context.getFeatureName()));
	}
}
