package io.github.saneea.feature;

import io.github.saneea.Feature;
import io.github.saneea.FeatureContext;

public class Sleep implements Feature {

	@Override
	public String getShortDescription() {
		return "wait some time before exit";
	}

	@Override
	public void run(FeatureContext context) throws InterruptedException {
		Thread.sleep(Long.parseLong(context.getArgs()[0]));
	}

}
