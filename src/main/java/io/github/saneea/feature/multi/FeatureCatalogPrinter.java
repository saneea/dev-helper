package io.github.saneea.feature.multi;

import java.io.PrintStream;

import io.github.saneea.FeatureContext;
import io.github.saneea.FeatureProvider;

public abstract class FeatureCatalogPrinter {
	protected final PrintStream out;

	public FeatureCatalogPrinter(PrintStream out) {
		this.out = out;
	}

	abstract void print(FeatureProvider featureProvider, FeatureContext context);
}
