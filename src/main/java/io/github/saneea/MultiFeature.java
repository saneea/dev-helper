package io.github.saneea;

import io.github.saneea.feature.help.HelpFeature;

public abstract class MultiFeature implements Feature {
	public abstract FeatureProvider getFeatureProvider();

	public void run(FeatureContext context) throws Exception {
		String[] args = context.getArgs();
		String featureName = args.length != 0//
				? args[0]//
				: HelpFeature.Alias.SHORT;

		FeatureRunner featureRunner = new FeatureRunner(getFeatureProvider());
		featureRunner.run(context, featureName, withoutFeatureName(args));
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
