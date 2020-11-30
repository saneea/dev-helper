package io.github.saneea.dvh;

import io.github.saneea.dvh.feature.DvhRootFeature;

public class App {

	public static void main(String[] args) throws Exception {

		DvhRootFeature rootFeature = new DvhRootFeature();

		try {
			rootFeature.run(new FeatureContext(null, "dvh", args));
		} catch (AppExitException appExitException) {
			System.exit(appExitException.getExitCode());
		}
	}

}
