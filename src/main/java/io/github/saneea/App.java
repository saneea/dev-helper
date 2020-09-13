package io.github.saneea;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class App {

	private final AppContext appContext;

	private App() throws IOException {
		this.appContext = new AppContext();
	}

	public static void main(String[] args) throws Exception {
		String featureName = args.length != 0 ? args[0] : "help";

		App app = new App();
		app.run(featureName, withoutFeatureName(args));
	}

	private void run(String featureName, String[] args) throws Exception {
		Feature feature = appContext.createFeature(featureName);
		runFeature(feature, args);
	}

	private void runFeature(Feature feature, String[] args) throws Exception {
		try (InputStream input = new BufferedInputStream(System.in); //
				OutputStream output = new BufferedOutputStream(System.out)) {
			feature.run(new FeatureContext(args, input, output, System.err, appContext));
		}
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
