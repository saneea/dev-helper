package io.github.saneea.feature;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;

import io.github.saneea.Feature;

public class JsonPrettyPrint implements Feature {

	@Override
	public void run(InputStream input, OutputStream output, String[] args) throws Exception {
		Gson gson = new GsonBuilder()//
				.setPrettyPrinting()//
				.create();

		JsonElement jsonElement = gson.fromJson(new InputStreamReader(input), JsonElement.class);

		try (OutputStreamWriter writer = new OutputStreamWriter(output)) {
			gson.toJson(jsonElement, writer);
		}
	}

}
