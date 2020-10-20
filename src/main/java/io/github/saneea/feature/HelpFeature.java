package io.github.saneea.feature;

import java.io.PrintStream;
import java.util.Set;

import io.github.saneea.Feature;
import io.github.saneea.FeatureContext;
import io.github.saneea.FeatureProvider;

public class HelpFeature implements Feature, Feature.Out.Text.PrintStream {

	public interface Alias {
		String SHORT = "-h";
		String LONG = "--help";
	}

	private PrintStream out;

	@Override
	public String getShortDescription() {
		return "print dev-helper usage guide";
	}

	@Override
	public void run(FeatureContext context) throws Exception {

		FeatureProvider featureProvider = context.getFeatureProvider();

		out.println("usage:");
		out.println("\t" + getFeaturesChain(context.getParentContext()) + "<feature name> [feature args]");
		out.println();
		out.println("available features:");

		Set<String> featuresNames = featureProvider.featuresNames();

		int maxFeatureNameSize = featuresNames.stream().mapToInt(String::length).max().orElse(0);

		for (String featureName : featuresNames) {
			Feature feature = featureProvider.createFeature(featureName);
			String featureShortDescription = feature.getShortDescription();
			String template = "\t%1$" + maxFeatureNameSize + "s - %2$s";

			out.println(String.format(template, featureName, featureShortDescription));
		}
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

}
