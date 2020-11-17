package io.github.saneea;

import java.io.PrintStream;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import io.github.saneea.FeatureContext.Parent;
import io.github.saneea.feature.help.FeatureTree;
import io.github.saneea.textfunction.Utils;

public abstract class MultiFeature implements Feature {

	public static final String CATALOG = "catalog";
	public static final String CATALOG_HIDE = "hide";
	public static final String CATALOG_LIST = "list";
	public static final String CATALOG_TREE = "tree";

	public interface HelpAlias {
		String SHORT = "-h";
		String LONG = "--help";
	}

	private PrintStream out = System.out;

	public abstract FeatureProvider getFeatureProvider();

	public void run(FeatureContext context) throws Exception {
		String[] args = context.getArgs();

		String featureName = args.length != 0//
				? args[0]//
				: HelpAlias.SHORT;

		String[] featureArgs = withoutFeatureName(args);

		if (HelpAlias.SHORT.equals(featureName)//
				|| HelpAlias.LONG.equals(featureName)) {
			Options cliOptions = new Options();
			for (Option cliOption : getCliOptions()) {
				cliOptions.addOption(cliOption);
			}
			CommandLine commandLine = Utils.parseCli(context.getFeatureName(), featureArgs, cliOptions);
			FeatureContext childContext = new FeatureContext(//
					new FeatureContext.Parent(//
							context, getFeatureProvider()), //
					featureName, args);
			runHelpFeature(childContext, commandLine);
		} else {
			FeatureRunner featureRunner = new FeatureRunner(getFeatureProvider());
			featureRunner.run(context, featureName, featureArgs);
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

	private void runHelpFeature(FeatureContext context, CommandLine commandLine) throws Exception {

		FeatureContext.Parent parent = context.getParent();

		out.println("usage:");
		out.println("\t" + getFeaturesChain(parent.getContext()) + "<feature name> [feature args]");

		FeatureProvider parentFeatureProvider = parent.getFeatureProvider();

		String catalogMode = commandLine.getOptionValue(CATALOG, CATALOG_LIST);

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

	private void printCatalogAsList(FeatureProvider parentFeatureProvider) throws Exception {
		out.println();
		out.println("available features:");

		Set<String> featuresNames = parentFeatureProvider.featuresNames();

		int maxFeatureNameSize = getMaxStringLength(featuresNames);

		for (String featureName : featuresNames) {
			Feature feature = parentFeatureProvider.createFeature(featureName);
			String featureShortDescription = feature.getShortDescription();
			String template = "\t%1$" + maxFeatureNameSize + "s - %2$s";

			out.println(String.format(template, featureName, featureShortDescription));
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

	private void printCatalogAsTree(FeatureProvider parentFeatureProvider) throws Exception {
		out.println();
		out.println("available features:");

		FeatureTree featureTree = new FeatureTree("dvh", "dvh");
		buildFeatureTree(featureTree, parentFeatureProvider);

		printCatalogTreeBranch(featureTree, "\t");
	}

	private void buildFeatureTree(FeatureTree parent, FeatureProvider featureProvider) throws Exception {
		Set<String> featuresNames = featureProvider.featuresNames();

		for (String featureName : featuresNames) {
			Feature feature = featureProvider.createFeature(featureName);
			String featureShortDescription = feature.getShortDescription();

			FeatureTree child = new FeatureTree(featureName, featureShortDescription);
			parent.getChildren().add(child);

			if (feature instanceof MultiFeature) {
				MultiFeature multiFeature = (MultiFeature) feature;
				buildFeatureTree(child, multiFeature.getFeatureProvider());
			}
		}
	}

	private void printCatalogTreeBranch(FeatureTree featureTree, String levelLineSuffix) throws Exception {

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
}
