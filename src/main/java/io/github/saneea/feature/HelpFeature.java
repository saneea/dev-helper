package io.github.saneea.feature;

import java.io.PrintStream;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

import io.github.saneea.AppContext;
import io.github.saneea.Feature;
import io.github.saneea.FeatureContext;

public class HelpFeature implements Feature {

	@Override
	public String getShortDescription() {
		return "print dev-helper usage guide";
	}

	@Override
	public void run(FeatureContext context) throws Exception {

		AppContext appContext = context.getAppContext();

		Properties featureAlias = appContext.getFeatureAlias();

		try (PrintStream writer = new PrintStream(context.getOut())) {

			writer.println("usage:");
			writer.println("\tdvh <feature name> [feature args]");
			writer.println();
			writer.println("available features:");

			Set<String> featuresNames = featureAlias.stringPropertyNames();

			int maxFeatureNameSize = featuresNames.stream().mapToInt(String::length).max().orElse(0);

			for (String featureName : new TreeSet<String>(featuresNames)) {
				Feature feature = appContext.createFeature(featureName);
				String featureShortDescription = feature.getShortDescription();
				String template = "\t%1$" + maxFeatureNameSize + "s - %2$s";

				writer.println(String.format(template, featureName, featureShortDescription));
			}
		}

	}

}
