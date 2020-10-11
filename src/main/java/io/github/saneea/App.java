package io.github.saneea;

import io.github.saneea.textfunction.Utils;

public class App {

	public static void main(String[] args) throws Exception {
		try {
			String featureName = args.length != 0 ? args[0] : "help";

			FeatureRunner featureRunner = new RootFeatureRunner(new AppContext());
			featureRunner.run(featureName, Utils.withoutFeatureName(args));
		} catch (AppExitException appExitException) {
			System.exit(appExitException.getExitCode());
		}
	}

}
