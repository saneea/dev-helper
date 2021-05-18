package io.github.saneea.dvh

import java.util.stream.Collectors
import java.util.stream.Stream

class FeatureContext(
    private val parent: FeatureContext?,
    val featureName: String,
    val args: Array<String>
) {
    val featuresChainString: String
        get() = featuresChain.collect(Collectors.joining(" "))

    val featuresChain: Stream<String>
        get() = Stream.concat(
            parent?.featuresChain ?: Stream.empty(),
            Stream.of(featureName)
        )
}