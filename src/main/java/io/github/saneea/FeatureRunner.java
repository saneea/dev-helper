package io.github.saneea;

import java.io.IOException;

public class FeatureRunner {

	private final FeatureProvider featureProvider;

	public FeatureRunner(FeatureProvider featureProvider) {
		this.featureProvider = featureProvider;
	}

	public void run(FeatureContext context, String featureName, String[] args) throws Exception {
		Feature feature = featureProvider.createFeature(featureName);
		if (feature == null) {
			throw new IllegalArgumentException("Unknown feature: \"" + featureName + "\"");
		}

		runFeature(context, feature, featureName, args);
	}

	private void runFeature(//
			FeatureContext context, //
			Feature feature, //
			String featureName, //
			String[] args) throws Exception {
		FeatureContext childContext = new FeatureContext(context, featureName, args);
		try (FeatureResources featureResources = handleFeatureResources(feature, args, childContext)) {
			feature.run(childContext);
		}
	}

	private FeatureResources handleFeatureResources(Feature feature, String[] args, FeatureContext context)
			throws IOException {

		FeatureResources featureResources = new FeatureResources(feature, args, context);

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