package io.github.saneea.dvh

import io.github.saneea.dvh.feature.multi.FeatureInfo

interface FeatureProvider {

    fun featuresNames(): Set<String>

    fun createFeature(featureName: String): Feature?

    fun featureInfo(context: FeatureContext): FeatureInfo {
        return try {
            val feature = createFeature(context.featureName, context)
            FeatureInfo(feature, feature!!.meta.description.brief)
        } catch (e: Exception) {
            FeatureInfo(null, e.toString())
        }
    }
}

fun FeatureProvider.createFeature(featureName: String, context: FeatureContext) =
    this.createFeature(featureName).also { it?.context = context }