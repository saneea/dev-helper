package io.github.saneea.feature.json;

import java.io.Reader;
import java.io.Writer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;

import io.github.saneea.Feature;
import io.github.saneea.FeatureContext;

public class JsonFormatting {

	public static class Pretty extends Base {

		@Override
		public String getShortDescription() {
			return "format JSON with indents";
		}

		@Override
		protected Gson gson() {
			return new GsonBuilder()//
					.setPrettyPrinting()//
					.create();
		}
	}

	public static class Line extends Base {

		@Override
		public String getShortDescription() {
			return "format JSON to line";
		}

		@Override
		protected Gson gson() {
			return new GsonBuilder()//
					.create();
		}
	}

	private static abstract class Base implements Feature, Feature.In.Text.Reader, Feature.Out.Text.Writer {

		private Reader in;
		private Writer out;

		protected abstract Gson gson();

		@Override
		public void run(FeatureContext context) throws Exception {
			Gson gson = gson();
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

}
