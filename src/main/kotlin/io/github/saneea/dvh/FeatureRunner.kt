package io.github.saneea.dvh

fun runFeature(context: FeatureContext, featureProvider: FeatureProvider, featureName: String, args: List<String>) {
    val childContext = FeatureContext(context, featureName, args)

    val feature = featureProvider.createFeature(featureName, childContext)
        ?: throw IllegalArgumentException("Unknown feature: \"$featureName\"")

    FeatureResources(feature, args, childContext).use {
        feature.injectResources(it)
        feature.run()
    }
}

private fun Feature.injectResources(featureResources: FeatureResources) {
    (this as? Feature.CLI)
        ?.commandLine = featureResources.commandLine

    (this as? Feature.Out.Text.PrintStream)
        ?.outTextPrintStream = featureResources.outTextPrintStream

    (this as? Feature.Out.Text.Writer)
        ?.outTextWriter = featureResources.outTextWriter

    (this as? Feature.Out.Text.String)
        ?.outTextString = featureResources.outTextString

    (this as? Feature.Out.Bin.Stream)
        ?.outBinStream = featureResources.outBinStream

    (this as? Feature.Err.Bin.Stream)
        ?.errBinStream = featureResources.errBinStream

    (this as? Feature.In.Text.Reader)
        ?.inTextReader = featureResources.inTextReader

    (this as? Feature.In.Text.String)
        ?.inTextString = featureResources.inTextString

    (this as? Feature.In.Bin.Stream)
        ?.inBinStream = featureResources.inBinStream
}
