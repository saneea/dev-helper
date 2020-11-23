package io.github.saneea.feature.multi;

import java.io.PrintStream;
import java.util.Set;

import io.github.saneea.FeatureContext;
import io.github.saneea.FeatureProvider;
import io.github.saneea.textfunction.Utils;

public class ListPrinter extends FeatureCatalogPrinter {

	public ListPrinter(PrintStream out) {
		super(out);
	}

	@Override
	public void print(FeatureProvider featureProvider, FeatureContext context) {

		Set<String> featuresNames = featureProvider.featuresNames();

		int maxFeatureNameSize = Utils.getMaxStringLength(featuresNames);

		for (String featureName : featuresNames) {
			FeatureInfo featureInfo = featureProvider.featureInfo(featureName);
			String template = "%1$" + maxFeatureNameSize + "s - %2$s";

			out.println(String.format(template, featureName, featureInfo.description));
		}
	}

}
