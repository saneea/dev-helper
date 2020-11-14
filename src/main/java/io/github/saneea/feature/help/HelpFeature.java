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
import io.github.saneea.FeatureContext.Parent;
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
