package io.github.saneea.feature;

import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;

import io.github.saneea.Feature;
import io.github.saneea.FeatureContext;

public class JsonPrettyPrint implements Feature {

	@Override
	public void run(FeatureContext context) throws Exception {
		Gson gson = new GsonBuilder()//
				.setPrettyPrinting()//
				.create();

		JsonElement jsonElement = gson.fromJson(new InputStreamReader(context.getIn()), JsonElement.class);

		try (OutputStreamWriter writer = new OutputStreamWriter(context.getOut())) {
			gson.toJson(jsonElement, writer);
		}
	}

}
