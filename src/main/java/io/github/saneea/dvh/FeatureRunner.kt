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

        (feature as? Feature.CLI)
            ?.commandLine = featureResources.commandLine

        (feature as? Feature.Out.Text.PrintStream)
            ?.outTextPrintStream = featureResources.outTextPrintStream

        (feature as? Feature.Out.Text.Writer)
            ?.outTextWriter = featureResources.outTextWriter

        (feature as? Feature.Out.Text.String)
            ?.outTextString = featureResources.outTextString

        (feature as? Feature.Out.Bin.Stream)
            ?.outBinStream = featureResources.outBinStream

        (feature as? Feature.Err.Bin.Stream)
            ?.errBinStream = featureResources.errBinStream

        (feature as? Feature.In.Text.Reader)
            ?.inTextReader = featureResources.inTextReader

        (feature as? Feature.In.Text.String)
            ?.inTextString = featureResources.inTextString

        (feature as? Feature.In.Bin.Stream)
            ?.inBinStream = featureResources.inBinStream

        return featureResources
    }
}