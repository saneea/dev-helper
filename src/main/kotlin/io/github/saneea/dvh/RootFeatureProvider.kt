package io.github.saneea.dvh

import io.github.saneea.dvh.utils.Utils
import java.nio.charset.StandardCharsets
import java.util.*

const val FEATURE_ALIASES_PROPS_FILE_NAME = "/feature-aliases.properties"

class RootFeatureProvider : FeatureProvider {

    private val featureAliases: Map<String, String> = Utils.toMap(loadProperties())

    override fun featuresNames() = featureAliases.keys

    override fun createFeature(featureName: String) =
        featureAliases[featureName]
            ?.let { createFeatureInstance(Class.forName(it)) }

    private fun createFeatureInstance(featureClass: Class<*>): Feature {
        val featureObj = featureClass.getDeclaredConstructor().newInstance()
        require(featureObj is Feature) {
            "Class ${featureObj.javaClass} does not implement ${Feature::class.java}"
        }
        return featureObj
    }

    companion object {

        private fun loadProperties(): Properties {
            val featureAliases = Properties()

            val resourceAsStream = App::class.java.getResourceAsStream(FEATURE_ALIASES_PROPS_FILE_NAME)

            requireNotNull(resourceAsStream) {
                "Feature aliases file is not found: $FEATURE_ALIASES_PROPS_FILE_NAME"
            }
                .buffered()
                .reader(StandardCharsets.UTF_8)
                .use(featureAliases::load)

            return featureAliases
        }
    }

}