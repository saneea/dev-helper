package io.github.saneea;

public interface Feature {

	void run(FeatureContext context) throws Exception;

	String getShortDescription();
}
