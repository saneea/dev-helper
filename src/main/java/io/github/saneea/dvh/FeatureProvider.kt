package io.github.saneea.dvh

import io.github.saneea.dvh.feature.multi.FeatureInfo

interface FeatureProvider {

    fun featuresNames(): Set<String>

    fun createFeature(featureName: String): Feature?

    fun featureInfo(context: FeatureContext): FeatureInfo {
        return try {
            val feature = createFeature(context.featureName)
            FeatureInfo(feature, feature!!.meta(context).description.brief)
        } catch (e: Exception) {
            FeatureInfo(null, e.toString())
        }
    }
}