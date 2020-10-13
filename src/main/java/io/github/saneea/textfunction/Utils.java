package io.github.saneea.textfunction;

import java.util.Map;
import java.util.Properties;
import java.util.function.Function;
import java.util.stream.Collectors;

import io.github.saneea.FeatureContext;
import io.github.saneea.FeatureProvider;
import io.github.saneea.FeatureRunner;
import io.github.saneea.feature.HelpFeature;

public class Utils {

	public static void dvhEntryPoint(FeatureContext parentFeatureContext, FeatureProvider featureProvider)
			throws Exception {
		String[] args = parentFeatureContext.getArgs();
		String featureName = args.length != 0//
				? args[0]//
				: HelpFeature.Alias.SHORT;

		FeatureRunner featureRunner = new FeatureRunner(featureProvider);
		featureRunner.run(parentFeatureContext, featureName, withoutFeatureName(args));
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

	public static Map<String, String> toMap(Properties properties) {
		return properties//
				.stringPropertyNames()//
				.stream()//
				.collect(//
						Collectors.toMap(//
								Function.identity(), //
								properties::getProperty));
	}
}
