package io.github.saneea;

import java.io.IOException;

import io.github.saneea.feature.HelpFeature;

public class FeatureRunner {

	private final FeatureProvider featureProvider;

	public FeatureRunner(FeatureProvider featureProvider) {
		this.featureProvider = featureProvider;
	}

	public void run(FeatureContext parentFeatureContext, String featureName, String[] args) throws Exception {
		Feature feature = createFeature(featureName);
		if (feature == null) {
			throw new IllegalArgumentException("Unknown feature: \"" + featureName + "\"");
		}

		runFeature(parentFeatureContext, feature, featureName, args);
	}

	private Feature createFeature(String featureName) throws Exception {
		switch (featureName) {
		case HelpFeature.Alias.SHORT:
		case HelpFeature.Alias.LONG:
			return new HelpFeature();

		default:
			return featureProvider.createFeature(featureName);
		}
	}

	private void runFeature(//
			FeatureContext parentFeatureContext, //
			Feature feature, //
			String featureName, //
			String[] args) throws Exception {
		try (FeatureResources featureResources = handleFeatureResources(feature, featureName, args)) {
			feature.run(new FeatureContext(parentFeatureContext, featureName, args, featureProvider));
		}
	}

	private FeatureResources handleFeatureResources(Feature feature, String featureName, String[] args)
			throws IOException {

		FeatureResources featureResources = new FeatureResources(feature, featureName, args);

		if (feature instanceof Feature.CLI) {
			((Feature.CLI) feature)//
					.setCommandLine(featureResources.getCommandLine());
		}

		if (feature instanceof Feature.Out.Text.PrintStream) {
			((Feature.Out.Text.PrintStream) feature)//
					.setOut(//
							featureResources.getOutTextPrintStream());
		}

		if (feature instanceof Feature.Out.Text.Writer) {
			((Feature.Out.Text.Writer) feature)//
					.setOut(//
							featureResources.getOutTextWriter());
		}

		if (feature instanceof Feature.Out.Text.String) {
			((Feature.Out.Text.String) feature)//
					.setOut(//
							featureResources.getOutTextString());
		}

		if (feature instanceof Feature.Out.Bin.Stream) {
			((Feature.Out.Bin.Stream) feature)//
					.setOut(//
							featureResources.getOutBinStream());
		}

		if (feature instanceof Feature.Err.Bin.Stream) {
			((Feature.Err.Bin.Stream) feature)//
					.setErr(//
							featureResources.getErrBinStream());
		}

		if (feature instanceof Feature.In.Text.Reader) {
			((Feature.In.Text.Reader) feature)//
					.setIn(//
							featureResources.getInTextReader());
		}

		if (feature instanceof Feature.In.Text.String) {
			((Feature.In.Text.String) feature)//
					.setIn(//
							featureResources.getInTextString());
		}

		if (feature instanceof Feature.In.Bin.Stream) {
			((Feature.In.Bin.Stream) feature)//
					.setIn(//
							featureResources.getInBinStream());
		}

		return featureResources;
	}
}