package io.github.saneea.feature;

import java.util.Map;
import java.util.function.Supplier;

import io.github.saneea.Feature;
import io.github.saneea.Feature.Util.IOConsumer;
import io.github.saneea.FeatureContext;

public class Time extends MultiFeatureBase {

	private static final int MILLIS_IN_SECOND = 1000;

	@Override
	public String getShortDescription() {
		return "fork process";
	}

	@Override
	public Map<String, Supplier<Feature>> getFeatureAliases() {
		return new AliasesBuilder()//
				.feature("now", Now::new)//
				.build();
	}

	private static class Now implements Feature, Feature.Out.Text.String {

		IOConsumer<String> out;

		@Override
		public String getShortDescription() {
			return "print current time";
		}

		@Override
		public void run(FeatureContext context) throws Exception {
			long millis = System.currentTimeMillis();

			long seconds = millis / MILLIS_IN_SECOND;

			out.accept(//
					String.valueOf(//
							seconds));
		}

		@Override
		public void setOut(IOConsumer<String> out) {
			this.out = out;
		}

	}
}
