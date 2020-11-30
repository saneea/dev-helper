package io.github.saneea.dvh.feature.multi;

import java.io.PrintStream;

import io.github.saneea.dvh.FeatureContext;
import io.github.saneea.dvh.FeatureProvider;

public abstract class FeatureCatalogPrinter {
	protected final PrintStream out;

	public FeatureCatalogPrinter(PrintStream out) {
		this.out = out;
	}

	abstract void print(FeatureProvider featureProvider, FeatureContext context);
}
