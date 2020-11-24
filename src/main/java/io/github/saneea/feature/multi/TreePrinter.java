package io.github.saneea.feature.multi;

import java.io.PrintStream;
import java.util.List;
import java.util.Set;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import io.github.saneea.FeatureContext;
import io.github.saneea.FeatureProvider;
import io.github.saneea.textfunction.Utils;

public class TreePrinter extends FeatureCatalogPrinter {

	private static final int DASH_LENGTH = 3;
	private static final String DASH_STRING = "-";
	private static final String SPACE_STRING = " ";
	private static final String DASH_FOR_NODE = Utils.repeatString(DASH_STRING, DASH_LENGTH);
	private static final String SPACE_FOR_NODE = Utils.repeatString(SPACE_STRING, DASH_LENGTH);

	public TreePrinter(PrintStream out) {
		super(out);
	}

	@Override
	public void print(FeatureProvider featureProvider, FeatureContext context) {
		List<FeatureTree> featuresChain = context.getFeaturesChain()//
				.map(featureName -> new FeatureTree(featureName, ""))//
				.collect(Collectors.toList());

		for (int i = 0; i < featuresChain.size() - 1; ++i) {
			featuresChain.get(i).getChildren().add(featuresChain.get(i + 1));
		}

		buildFeatureTree(featuresChain.get(featuresChain.size() - 1), featureProvider, context);

		FeatureTree root = featuresChain.get(0);

		int maxFeatureNameSize = getMaxFeatureNameSize(root, 0);

		printCatalogChildBranch("", root, maxFeatureNameSize, true, 0);
	}

	private int getMaxFeatureNameSize(FeatureTree featureTree, int level) {
		return IntStream.concat(//

				IntStream.of(//
						featureTree//
								.getAlias()//
								.length() + getLevelOffset(level)), //

				featureTree.getChildren().stream()//
						.map(childBranch -> getMaxFeatureNameSize(childBranch, level + 1))//
						.mapToInt(identity()))//

				.max()//
				.getAsInt();
	}

	private void printCatalogTreeBranch(FeatureTree featureTree, String levelLineSuffix, int level,
			int maxFeatureNameSize) {

		List<FeatureTree> features = featureTree.getChildren();

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
			featureLine.append(DASH_FOR_NODE);
		}
		featureLine.append(featureName);

		if (!isHub) {
			featureLine//
					.append(Utils.repeatString(" ", maxFeatureNameSize - featureName.length() - getLevelOffset(level)))//
					.append(" - ")//
					.append(featureShortDescription);
		}

		out.println(featureLine);

		if (isHub) {
			StringBuilder nextLevelLineSuffix = new StringBuilder(levelLineSuffix);
			if (!isRoot) {
				nextLevelLineSuffix.append(last ? " " : "|");
				nextLevelLineSuffix.append(SPACE_FOR_NODE);
			}
			printCatalogTreeBranch(feature, nextLevelLineSuffix.toString(), level, maxFeatureNameSize);
		} else if (last) {
			out.println(levelLineSuffix);
		}
	}

	private int getLevelOffset(int level) {
		return (DASH_LENGTH + 1) * level;
	}

	private void buildFeatureTree(FeatureTree parent, FeatureProvider featureProvider, FeatureContext context) {
		Set<String> featuresNames = featureProvider.featuresNames();

		for (String featureName : featuresNames) {
			FeatureContext childContext = new FeatureContext(context, featureName, Utils.NO_ARGS);
			FeatureInfo featureInfo = featureProvider.featureInfo(childContext);

			FeatureTree child = new FeatureTree(featureName, featureInfo.description);
			parent.getChildren().add(child);

			if (featureInfo.feature instanceof MultiFeature) {
				MultiFeature multiFeature = (MultiFeature) featureInfo.feature;
				buildFeatureTree(child, multiFeature.getFeatureProvider(), childContext);
			}
		}
	}

	private static ToIntFunction<Integer> identity() {
		return i -> i;
	}
}
