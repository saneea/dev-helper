package io.github.saneea;

import io.github.saneea.textfunction.Utils;

public class App {

	public static void main(String[] args) throws Exception {
		try {
			Utils.dvhEntryPoint(args, new RootFeatureProvider());
		} catch (AppExitException appExitException) {
			System.exit(appExitException.getExitCode());
		}
	}

}
