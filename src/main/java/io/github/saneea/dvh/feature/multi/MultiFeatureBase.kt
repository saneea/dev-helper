package io.github.saneea.dvh.feature.multi

import io.github.saneea.dvh.Feature
import io.github.saneea.dvh.Feature.Meta
import io.github.saneea.dvh.FeatureContext
import io.github.saneea.dvh.FeatureProvider
import java.util.function.Supplier

abstract class MultiFeatureBase : MultiFeature() {

    override val featureProvider: FeatureProvider
        get() = MultiFeatureProvider(getFeatureAliases())

    abstract fun getFeatureAliases(): Map<String, Supplier<Feature>>

    class AliasesBuilder {
        private val aliases: MutableMap<String, Supplier<Feature>> = LinkedHashMap()
        fun feature(featureAlias: String, featureCtor: Supplier<Feature>): AliasesBuilder {
            aliases[featureAlias] = featureCtor
            return this
        }

        fun multiFeature( //
            featureAlias: String,  //
            shortDescription: String?,  //
            children: Map<String, Supplier<Feature>>
        ): AliasesBuilder {

            return feature(featureAlias) {
                object : MultiFeatureBase() {
                    override fun meta(context: FeatureContext): Meta {
                        return Meta.from(shortDescription)
                    }

                    override fun getFeatureAliases() = children
                }
            }
        }

        fun build(): Map<String, Supplier<Feature>> {
            return aliases
        }
    }

    private class MultiFeatureProvider(private val featureAliases: Map<String, Supplier<Feature>>) : FeatureProvider {
        override fun featuresNames(): Set<String> {
            return featureAliases.keys
        }

        override fun createFeature(featureName: String) = featureAliases[featureName]?.get()
    }
}