package io.github.saneea;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import io.github.saneea.feature.FilteredComparison;
import io.github.saneea.feature.Gui;
import io.github.saneea.feature.JsonPrettyPrint;
import io.github.saneea.feature.PrintArgsFeature;
import io.github.saneea.feature.SystemProcessFeature;
import io.github.saneea.feature.ToFile;
import io.github.saneea.feature.UpperCase;
import io.github.saneea.feature.XmlPrettyPrint;
import io.github.saneea.feature.XmlReform;
import io.github.saneea.feature.XmlToLine;

public class App {

	enum FeatureAlias {
		GUI("gui", Gui.class), //
		XML_TO_LINE("xmlToLine", XmlToLine.class), //
		XML_PRETTY_PRINT("xmlPrettyPrint", XmlPrettyPrint.class), //
		JSON_PRETTY_PRINT("jsonPrettyPrint", JsonPrettyPrint.class), //
		UPPER_CASE("upperCase", UpperCase.class), //
		TO_FILE("toFile", ToFile.class), //
		SYSTEM_PROCESS_FEATURE("systemProcess", SystemProcessFeature.class), //
		PRINT_ARGS_FEATURE("printArgs", PrintArgsFeature.class), //
		XML_REFORM("xmlReform", XmlReform.class), //
		FILTERED_COMPARISON("filteredComparison", FilteredComparison.class);

		public final String asString;

		public final Class<? extends Feature> featureClass;

		FeatureAlias(String asString, Class<? extends Feature> featureClass) {
			this.asString = asString;
			this.featureClass = featureClass;
		}
	}

	public static void main(String[] args) throws Exception {
		String featureName = args.length != 0 ? args[0] : "gui";

		Class<? extends Feature> featureClass = getFeatureClass(featureName);

		Feature feature = featureClass.newInstance();

		try (InputStream input = new BufferedInputStream(System.in); //
				OutputStream output = new BufferedOutputStream(System.out)) {
			feature.run(input, output, withoutFeatureName(args));
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

	private static Class<? extends Feature> getFeatureClass(String featureName) {
		for (FeatureAlias feature : FeatureAlias.values()) {
			if (feature.asString.equals(featureName)) {
				return feature.featureClass;
			}
		}
		throw new IllegalArgumentException("Unknown feature: \"" + featureName + "\"");
	}

}
