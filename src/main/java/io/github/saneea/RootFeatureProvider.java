package io.github.saneea;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class RootFeatureProvider extends FeatureProvider {
	private final Properties featureAlias;

	public RootFeatureProvider() throws IOException {
		this.featureAlias = new Properties();
		InputStream resourceAsStream = App.class.getResourceAsStream("/feature-alias.properties");
		try (Reader reader = new InputStreamReader(//
				new BufferedInputStream(//
						resourceAsStream), //
				StandardCharsets.UTF_8)) {
			featureAlias.load(reader);
		}
	}

	@Override
	public Properties getFeatureAlias() {
		return featureAlias;
	}

	@Override
	public Feature createFeature(String featureName) throws ClassNotFoundException, InstantiationException,
			IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		Class<?> featureClass = getFeatureClass(featureName);
		return createFeatureInstance(featureClass);
	}

	private Class<?> getFeatureClass(String featureName) throws ClassNotFoundException {
		String featureClassName = featureAlias.getProperty(featureName);

		if (featureClassName == null) {
			throw new IllegalArgumentException("Unknown feature: \"" + featureName + "\"");
		}

		return Class.forName(featureClassName);
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
}
