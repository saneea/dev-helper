package io.github.saneea.feature.help;

import java.io.PrintStream;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

import io.github.saneea.Feature;
import io.github.saneea.FeatureContext;
import io.github.saneea.FeatureProvider;
import io.github.saneea.MultiFeature;

public class HelpFeature implements//
		Feature, //
		Feature.Out.Text.PrintStream, //
		Feature.CLI {

	public static final String CATALOG = "catalog";
	public static final String CATALOG_HIDE = "hide";
	public static final String CATALOG_LIST = "list";
	public static final String CATALOG_TREE = "tree";

	public interface Alias {
		String SHORT = "-h";
		String LONG = "--help";
	}

	private PrintStream out;
	private CommandLine commandLine;

	@Override
	public String getShortDescription() {
		return "print dev-helper usage guide";
	}

	@Override
	public void run(FeatureContext context) throws Exception {

		out.println("usage:");
		out.println("\t" + getFeaturesChain(context.getParentContext()) + "<feature name> [feature args]");

		String catalogMode = commandLine.getOptionValue(CATALOG, CATALOG_LIST);

		switch (catalogMode) {
		case CATALOG_HIDE:
			break;

		case CATALOG_LIST:
			printCatalogAsList(context);
			break;

		case CATALOG_TREE:
			printCatalogAsTree(context);
			break;
		}
	}

	private void printCatalogAsList(FeatureContext context) throws Exception {
		FeatureProvider featureProvider = context.getFeatureProvider();

		out.println();
		out.println("available features:");

		Set<String> featuresNames = featureProvider.featuresNames();

		int maxFeatureNameSize = getMaxStringLength(featuresNames);

		for (String featureName : featuresNames) {
			Feature feature = featureProvider.createFeature(featureName);
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

	private void printCatalogAsTree(FeatureContext context) throws Exception {
		FeatureProvider featureProvider = context.getFeatureProvider();

		out.println();
		out.println("available features:");

		FeatureTree featureTree = new FeatureTree("dvh", "dvh");
		buildFeatureTree(featureTree, featureProvider);

		printCatalogTreeBranch(featureTree, 0);
	}

	private void buildFeatureTree(FeatureTree parent, FeatureProvider featureProvider) throws Exception {
		Set<String> featuresNames = featureProvider.featuresNames();

		for (String featureName : featuresNames) {
			Feature feature = featureProvider.createFeature(featureName);
			String featureShortDescription = feature.getShortDescription();

			FeatureTree child = new FeatureTree(featureName, featureShortDescription);
			parent.getChildren().add(child);

			boolean isHub = feature instanceof MultiFeature;

			if (isHub) {
				MultiFeature multiFeature = (MultiFeature) feature;
				buildFeatureTree(child, multiFeature.getFeatureProvider());
			}
		}
	}

	private void printCatalogTreeBranch(FeatureTree featureTree, int level) throws Exception {

		List<FeatureTree> features = featureTree.getChildren();

		int maxFeatureNameSize = getMaxStringLength(//
				features//
						.stream()//
						.map(FeatureTree::getAlias));

		for (FeatureTree feature : features) {
			String featureName = feature.getAlias();
			String featureShortDescription = feature.getDescription();

			boolean isHub = !feature.getChildren().isEmpty();

			StringBuilder featureLine = new StringBuilder("\t")//
					.append(repeatString("|---", level))//
					.append(featureName);
			if (!isHub) {
				featureLine//
						.append(repeatString(" ", maxFeatureNameSize - featureName.length()))//
						.append(" - ")//
						.append(featureShortDescription);
			}

			out.println(featureLine);

			if (isHub) {
				printCatalogTreeBranch(feature, level + 1);
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
		return context != null//
				? getFeaturesChain(context.getParentContext()) + context.getFeatureName() + " "//
				: "";
	}

	@Override
	public void setOut(PrintStream out) {
		this.out = out;
	}

	@Override
	public void setCommandLine(CommandLine commandLine) {
		this.commandLine = commandLine;
	}

	@Override
	public Option[] getOptions() {
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

}
