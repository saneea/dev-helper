package io.github.saneea.feature.multi;

import java.io.PrintStream;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import io.github.saneea.FeatureContext;
import io.github.saneea.FeatureProvider;
import io.github.saneea.textfunction.Utils;

public class TreePrinter extends FeatureCatalogPrinter {

	public TreePrinter(PrintStream out) {
		super(out);
	}

	@Override
	public void print(FeatureProvider parentFeatureProvider, FeatureContext context) {

		List<FeatureTree> featuresChain = context.getParent().getContext().getFeaturesChain()//
				.map(featureName -> new FeatureTree(featureName, ""))//
				.collect(Collectors.toList());

		for (int i = 0; i < featuresChain.size() - 1; ++i) {
			featuresChain.get(i).getChildren().add(featuresChain.get(i + 1));
		}

		buildFeatureTree(featuresChain.get(featuresChain.size() - 1), parentFeatureProvider);

		printCatalogChildBranch("", featuresChain.get(0), 0, true, 0);
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

	private void buildFeatureTree(FeatureTree parent, FeatureProvider featureProvider) {
		Set<String> featuresNames = featureProvider.featuresNames();

		for (String featureName : featuresNames) {
			FeatureInfo featureInfo = featureProvider.featureInfo(featureName);

			FeatureTree child = new FeatureTree(featureName, featureInfo.description);
			parent.getChildren().add(child);

			if (featureInfo.feature instanceof MultiFeature) {
				MultiFeature multiFeature = (MultiFeature) featureInfo.feature;
				buildFeatureTree(child, multiFeature.getFeatureProvider());
			}
		}
	}
}
