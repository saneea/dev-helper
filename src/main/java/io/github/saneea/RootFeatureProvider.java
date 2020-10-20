package io.github.saneea;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import io.github.saneea.textfunction.Utils;

public class RootFeatureProvider implements FeatureProvider {
	private final Map<String, String> featureAliases;

	public RootFeatureProvider() throws IOException {
		this.featureAliases = Utils.toMap(loadProperties());
	}

	private static Properties loadProperties() throws IOException {
		Properties featureAliases = new Properties();
		InputStream resourceAsStream = App.class.getResourceAsStream("/feature-aliases.properties");
		try (Reader reader = new InputStreamReader(//
				new BufferedInputStream(//
						resourceAsStream), //
				StandardCharsets.UTF_8)) {
			featureAliases.load(reader);
		}
		return featureAliases;
	}

	@Override
	public Set<String> featuresNames() {
		return featureAliases.keySet();
	}

	@Override
	public Feature createFeature(String featureName) throws ClassNotFoundException, InstantiationException,
			IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		Class<?> featureClass = getFeatureClass(featureName);
		return featureClass == null//
				? null //
				: createFeatureInstance(featureClass);
	}

	private Class<?> getFeatureClass(String featureName) throws ClassNotFoundException {
		String featureClassName = featureAliases.get(featureName);
		return featureClassName == null//
				? null //
				: Class.forName(featureClassName);
	}

	private Feature createFeatureInstance(Class<?> featureClass)
			throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		Object featureObj = featureClass.getDeclaredConstructor().newInstance();

		if (!(featureObj instanceof Feature)) {
			throw new IllegalArgumentException(
					"Class " + featureObj.getClass() + " does not implement " + Feature.class);
		}

		return (Feature) featureObj;
	}
}
