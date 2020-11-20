package io.github.saneea;

import java.io.PrintStream;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import io.github.saneea.Feature.CLI.CommonOptions;
import io.github.saneea.FeatureContext.Parent;
import io.github.saneea.feature.help.FeatureTree;
import io.github.saneea.textfunction.Utils;
import io.github.saneea.textfunction.Utils.DefaultHelpPrinter;

public abstract class MultiFeature implements Feature {

	public static final String CATALOG = "catalog";
	public static final String CATALOG_HIDE = "hide";
	public static final String CATALOG_LIST = "list";
	public static final String CATALOG_TREE = "tree";

	private PrintStream out = System.out;

	public abstract FeatureProvider getFeatureProvider();

	public void run(FeatureContext context) throws Exception {
		String[] args = context.getArgs();

		if (args.length != 0 && !args[0].startsWith("-")) {
			String featureName = args[0];
			String[] featureArgs = withoutFeatureName(args);

			FeatureRunner featureRunner = new FeatureRunner(getFeatureProvider());
			featureRunner.run(context, featureName, featureArgs);
		} else {
			Options cliOptions = new Options();
			cliOptions.addOption(CommonOptions.HELP_OPTION);
			for (Option cliOption : getCliOptions()) {
				cliOptions.addOption(cliOption);
			}

			FeatureContext childContext = new FeatureContext(//
					new FeatureContext.Parent(//
							context, getFeatureProvider()), //
					null, args);

			MultiFeatureHelpPrinter helpPrinter = new MultiFeatureHelpPrinter(//
					context.getFeatureName(), cliOptions, childContext);

			CommandLine commandLine = Utils.parseCli(context.getFeatureName(), args, cliOptions, helpPrinter);

			helpPrinter.print(Optional.of(commandLine));
		}

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
					+ getFeaturesChain(childContext.getParent().getContext()) //
					+ "<feature name> [feature args]");
			out.println("--- or ---");

			super.print(commandLine);

			runHelpFeature(childContext, commandLine);
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

	private Option[] getCliOptions() {
		Option[] options = { //
				Option//
						.builder("c")//
						.longOpt(CATALOG)//
						.hasArg(true)//
						.argName(CATALOG_HIDE + '|' + CATALOG_LIST + '|' + CATALOG_TREE)//
						.required(false)//
						.desc("features catalog show mode")//
						.build() };
		return options;
	}

	private void runHelpFeature(FeatureContext context, Optional<CommandLine> commandLine) {

		FeatureContext.Parent parent = context.getParent();

		FeatureProvider parentFeatureProvider = parent.getFeatureProvider();

		String catalogMode = commandLine//
				.map(cl -> cl.getOptionValue(CATALOG))//
				.map(Optional::ofNullable)//
				.flatMap(Function.identity())//
				.orElse(CATALOG_LIST);

		switch (catalogMode) {
		case CATALOG_HIDE:
			break;

		case CATALOG_LIST:
			printCatalogAsList(parentFeatureProvider);
			break;

		case CATALOG_TREE:
			printCatalogAsTree(parentFeatureProvider);
			break;
		}
	}

	private void printCatalogAsList(FeatureProvider parentFeatureProvider) {
		out.println();
		out.println("available features:");

		Set<String> featuresNames = parentFeatureProvider.featuresNames();

		int maxFeatureNameSize = getMaxStringLength(featuresNames);

		for (String featureName : featuresNames) {
			FeatureInfo featureInfo = getFeatureInfo(parentFeatureProvider, featureName);
			String template = "\t%1$" + maxFeatureNameSize + "s - %2$s";

			out.println(String.format(template, featureName, featureInfo.description));
		}
	}

	private static int getMaxStringLength(Collection<String> strings) {
		return getMaxStringLength(strings.stream());
	}

	private static int getMaxStringLength(Stream<String> strings) {
		return strings//
				.mapToInt(String::length)//
				.max()//
				.orElse(0);
	}

	private void printCatalogAsTree(FeatureProvider parentFeatureProvider) {
		out.println();
		out.println("available features:");

		FeatureTree featureTree = new FeatureTree("dvh", "dvh");
		buildFeatureTree(featureTree, parentFeatureProvider);

		printCatalogTreeBranch(featureTree, "\t");
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

		Feature feature;
		String featureShortDescription;

		try {
			feature = featureProvider.createFeature(featureName);
			featureShortDescription = feature.getShortDescription();
		} catch (Exception e) {
			feature = null;
			featureShortDescription = e.toString();
		}

		return new FeatureInfo(feature, featureShortDescription);
	}

	private void printCatalogTreeBranch(FeatureTree featureTree, String levelLineSuffix) {

		List<FeatureTree> features = featureTree.getChildren();

		int maxFeatureNameSize = getMaxStringLength(//
				features//
						.stream()//
						.map(FeatureTree::getAlias));

		for (int i = 0; i < features.size(); ++i) {
			FeatureTree feature = features.get(i);
			boolean last = i == features.size() - 1;

			String featureName = feature.getAlias();
			String featureShortDescription = feature.getDescription();

			boolean isHub = !feature.getChildren().isEmpty();

			StringBuilder featureLine = new StringBuilder()//
					.append(levelLineSuffix)//
					.append(last ? "\\" : "+")//
					.append("---")//
					.append(featureName);

			if (!isHub) {
				featureLine//
						.append(repeatString(" ", maxFeatureNameSize - featureName.length()))//
						.append(" - ")//
						.append(featureShortDescription);
			}

			out.println(featureLine);

			if (isHub) {
				printCatalogTreeBranch(feature, levelLineSuffix + (last ? " " : "|") + "   ");
			} else if (last) {
				out.println(levelLineSuffix);
			}
		}
	}

	private static String repeatString(String s, int level) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < level; ++i) {
			sb.append(s);
		}
		return sb.toString();
	}

	private static String getFeaturesChain(FeatureContext context) {
		if (context == null) {
			return "";
		}

		Parent parent = context.getParent();
		FeatureContext parentContext = parent != null//
				? parent.getContext()//
				: null;

		return getFeaturesChain(parentContext) + context.getFeatureName() + " ";
	}

	private static class FeatureInfo {
		final Feature feature;
		final String description;

		public FeatureInfo(Feature feature, String description) {
			this.feature = feature;
			this.description = description;
		}
	}
}
