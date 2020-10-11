package io.github.saneea.textfunction;

import io.github.saneea.FeatureProvider;
import io.github.saneea.FeatureRunner;

public class Utils {

	public static void dvhEntryPoint(String[] args, FeatureProvider featureProvider) throws Exception {
		String featureName = args.length != 0 ? args[0] : "help";

		FeatureRunner featureRunner = new FeatureRunner(featureProvider);
		featureRunner.run(featureName, withoutFeatureName(args));
	}

	private static String[] withoutFeatureName(String[] args) {
		String[] newArgs;
		if (args.length == 0) {
			newArgs = args;
		} else {
			newArgs = new String[args.length - 1];
			System.arraycopy(args, 1, newArgs, 0, newArgs.length);
		}
		return newArgs;
	}
}
