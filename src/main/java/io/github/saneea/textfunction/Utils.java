package io.github.saneea.textfunction;

import java.util.Map;
import java.util.Properties;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Utils {

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
