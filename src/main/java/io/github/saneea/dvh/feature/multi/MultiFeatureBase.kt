package io.github.saneea.dvh.feature.multi

import io.github.saneea.dvh.Feature
import io.github.saneea.dvh.Feature.Meta
import io.github.saneea.dvh.FeatureContext
import io.github.saneea.dvh.FeatureProvider

typealias FeatureCreator = () -> Feature
typealias FeaturesCreators = Map<String, FeatureCreator>

abstract class MultiFeatureBase : MultiFeature() {

    override val featureProvider: FeatureProvider
        get() = MultiFeatureProvider(getFeatureAliases())

    abstract fun getFeatureAliases(): FeaturesCreators

    class AliasesBuilder {
        private val aliases: MutableMap<String, FeatureCreator> = LinkedHashMap()
        fun feature(featureAlias: String, featureCtor: FeatureCreator): AliasesBuilder {
            aliases[featureAlias] = featureCtor
            return this
        }

        fun multiFeature(
            featureAlias: String,
            shortDescription: String,
            children: FeaturesCreators
        ): AliasesBuilder {

            return feature(featureAlias) {
                object : MultiFeatureBase() {
                    override fun meta(context: FeatureContext) = Meta(shortDescription)

                    override fun getFeatureAliases() = children
                }
            }
        }

        fun build(): FeaturesCreators = aliases
    }

    private class MultiFeatureProvider(private val featureAliases: FeaturesCreators) : FeatureProvider {
        override fun featuresNames(): Set<String> {
            return featureAliases.keys
        }

        override fun createFeature(featureName: String) = featureAliases[featureName]?.invoke()
    }
}