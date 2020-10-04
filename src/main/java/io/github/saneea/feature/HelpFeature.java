package io.github.saneea.feature;

import java.io.PrintStream;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

import io.github.saneea.AppContext;
import io.github.saneea.Feature;
import io.github.saneea.FeatureContext;

public class HelpFeature implements Feature, Feature.Out.Text.PrintStream {

	private PrintStream out;

	@Override
	public String getShortDescription() {
		return "print dev-helper usage guide";
	}

	@Override
	public void run(FeatureContext context) throws Exception {

		AppContext appContext = context.getAppContext();

		Properties featureAlias = appContext.getFeatureAlias();

		out.println("usage:");
		out.println("\tdvh <feature name> [feature args]");
		out.println();
		out.println("available features:");

		Set<String> featuresNames = featureAlias.stringPropertyNames();

		int maxFeatureNameSize = featuresNames.stream().mapToInt(String::length).max().orElse(0);

		for (String featureName : new TreeSet<String>(featuresNames)) {
			Feature feature = appContext.createFeature(featureName);
			String featureShortDescription = feature.getShortDescription();
			String template = "\t%1$" + maxFeatureNameSize + "s - %2$s";

			out.println(String.format(template, featureName, featureShortDescription));
		}
	}

	@Override
	public void setOut(PrintStream out) {
		this.out = out;
	}

}
