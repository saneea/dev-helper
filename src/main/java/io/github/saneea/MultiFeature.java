package io.github.saneea;

import io.github.saneea.textfunction.Utils;

public interface MultiFeature extends Feature {
	FeatureProvider getFeatureProvider();

	default void run(FeatureContext context) throws Exception {
		Utils.dvhEntryPoint(//
				context, //
				getFeatureProvider());
	}
}
