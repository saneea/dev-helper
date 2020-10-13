package io.github.saneea;

import io.github.saneea.textfunction.Utils;

public class App {

	public static void main(String[] args) throws Exception {
		try {
			Utils.dvhEntryPoint(new FeatureContext(null, "dvh", args, null), new RootFeatureProvider());
		} catch (AppExitException appExitException) {
			System.exit(appExitException.getExitCode());
		}
	}

}
