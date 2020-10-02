package io.github.saneea.feature;

import java.io.Reader;
import java.io.Writer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;

import io.github.saneea.Feature;
import io.github.saneea.FeatureContext;

public class JsonPrettyPrint implements Feature, Feature.In.Text.Reader, Feature.Out.Text.Writer {

	private Reader in;
	private Writer out;

	@Override
	public String getShortDescription() {
		return "format JSON with indents";
	}

	@Override
	public void run(FeatureContext context) throws Exception {
		Gson gson = new GsonBuilder()//
				.setPrettyPrinting()//
				.create();

		JsonElement jsonElement = gson.fromJson(in, JsonElement.class);

		gson.toJson(jsonElement, out);
	}

	@Override
	public void setIn(Reader in) {
		this.in = in;
	}

	@Override
	public void setOut(Writer out) {
		this.out = out;
	}

}
