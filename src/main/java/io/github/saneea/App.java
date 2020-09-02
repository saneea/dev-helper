package io.github.saneea;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class App {

	private final Properties featureAlias;

	private App() throws IOException {
		featureAlias = new Properties();
		InputStream resourceAsStream = App.class.getResourceAsStream("/feature-alias.properties");
		try (Reader reader = new InputStreamReader(//
				new BufferedInputStream(//
						resourceAsStream), //
				StandardCharsets.UTF_8)) {
			featureAlias.load(reader);
		}
	}

	public static void main(String[] args) throws Exception {
		String featureName = args.length != 0 ? args[0] : "gui";

		App app = new App();
		app.run(featureName, withoutFeatureName(args));
	}

	private void run(String featureName, String[] args) throws Exception {
		Class<?> featureClass = getFeatureClass(featureName);

		Feature feature = createFeatureInstance(featureClass);

		runFeature(feature, args);
	}

	private void runFeature(Feature feature, String[] args) throws Exception {
		try (InputStream input = new BufferedInputStream(System.in); //
				OutputStream output = new BufferedOutputStream(System.out)) {
			feature.run(input, output, args);
		}
	}

	private Feature createFeatureInstance(Class<?> featureClass)
			throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		Object featureObj = featureClass.getDeclaredConstructor().newInstance();

		if (!(featureObj instanceof Feature)) {
			throw new IllegalArgumentException(
					"Class " + featureObj.getClass() + " does not implement " + Feature.class);
		}

		Feature feature = (Feature) featureObj;
		return feature;
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

	private Class<?> getFeatureClass(String featureName) throws ClassNotFoundException {
		String featureClassName = featureAlias.getProperty(featureName);

		if (featureClassName == null) {
			throw new IllegalArgumentException("Unknown feature: \"" + featureName + "\"");
		}

		return Class.forName(featureClassName);
	}

}
