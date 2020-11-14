package io.github.saneea;

import io.github.saneea.feature.DvhRootFeature;

public class App {

	public static void main(String[] args) throws Exception {

		DvhRootFeature rootFeature = new DvhRootFeature();

		try {
			rootFeature.run(new FeatureContext(null, "dvh", args, null));
		} catch (AppExitException appExitException) {
			System.exit(appExitException.getExitCode());
		}
	}

}
