package io.github.saneea.dvh.feature.json

import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import io.github.saneea.dvh.Feature
import io.github.saneea.dvh.Feature.Meta
import io.github.saneea.dvh.FeatureContext
import java.io.Reader
import java.io.Writer

class JsonFormatting {

    class Pretty : Base() {
        override val meta get() = Meta("format JSON with indents")

        override val gsonBuilder: GsonBuilder = GsonBuilder().setPrettyPrinting()
    }

    class Line : Base() {
        override val meta get() = Meta("format JSON to line")

        override val gsonBuilder = GsonBuilder()
    }

    abstract class Base :
        Feature,
        Feature.In.Text.Reader,
        Feature.Out.Text.Writer {

        override lateinit var context: FeatureContext
        override lateinit var inTextReader: Reader
        override lateinit var outTextWriter: Writer

        protected abstract val gsonBuilder: GsonBuilder

        override fun run() {
            val gson = gsonBuilder.create()
            val jsonElement = gson.fromJson(inTextReader, JsonElement::class.java)
            gson.toJson(jsonElement, outTextWriter)
        }
    }
}