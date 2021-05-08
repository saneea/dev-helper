package io.github.saneea.dvh

class FeatureRunner(private val featureProvider: FeatureProvider) {

    fun run(context: FeatureContext, featureName: String, args: Array<String>) {
        val feature = featureProvider.createFeature(featureName)
            ?: throw IllegalArgumentException("Unknown feature: \"$featureName\"")
        runFeature(context, feature, featureName, args)
    }

    private fun runFeature(
        context: FeatureContext,
        feature: Feature,
        featureName: String,
        args: Array<String>
    ) {
        val childContext = FeatureContext(context, featureName, args)
        handleFeatureResources(feature, args, childContext).use { feature.run(childContext) }
    }

    private fun handleFeatureResources(
        feature: Feature,
        args: Array<String>,
        context: FeatureContext
    ): FeatureResources {
        val featureResources = FeatureResources(feature, args, context)
        if (feature is Feature.CLI) {
            (feature as Feature.CLI)
                .setCommandLine(featureResources.commandLine)
        }
        if (feature is Feature.Out.Text.PrintStream) {
            (feature as Feature.Out.Text.PrintStream)
                .setOut(
                    featureResources.outTextPrintStream
                )
        }
        if (feature is Feature.Out.Text.Writer) {
            (feature as Feature.Out.Text.Writer)
                .setOut(
                    featureResources.outTextWriter
                )
        }
        if (feature is Feature.Out.Text.String) {
            (feature as Feature.Out.Text.String)
                .setOut(
                    featureResources.outTextString
                )
        }
        if (feature is Feature.Out.Bin.Stream) {
            (feature as Feature.Out.Bin.Stream)
                .setOut(
                    featureResources.outBinStream
                )
        }
        if (feature is Feature.Err.Bin.Stream) {
            (feature as Feature.Err.Bin.Stream)
                .setErr(
                    featureResources.errBinStream
                )
        }
        if (feature is Feature.In.Text.Reader) {
            (feature as Feature.In.Text.Reader)
                .setIn(
                    featureResources.inTextReader
                )
        }
        if (feature is Feature.In.Text.String) {
            (feature as Feature.In.Text.String)
                .setIn(
                    featureResources.inTextString
                )
        }
        if (feature is Feature.In.Bin.Stream) {
            (feature as Feature.In.Bin.Stream)
                .setIn(
                    featureResources.inBinStream
                )
        }
        return featureResources
    }
}