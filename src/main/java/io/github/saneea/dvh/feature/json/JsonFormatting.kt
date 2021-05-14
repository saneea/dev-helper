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
        override fun meta(context: FeatureContext) = Meta("format JSON with indents")

        override val gsonBuilder: GsonBuilder = GsonBuilder().setPrettyPrinting()
    }

    class Line : Base() {
        override fun meta(context: FeatureContext) = Meta("format JSON to line")

        override val gsonBuilder = GsonBuilder()
    }

    abstract class Base :
        Feature,
        Feature.In.Text.Reader,
        Feature.Out.Text.Writer {

        private lateinit var `in`: Reader
        private lateinit var out: Writer

        protected abstract val gsonBuilder: GsonBuilder

        override fun run(context: FeatureContext) {
            val gson = gsonBuilder.create()
            val jsonElement = gson.fromJson(`in`, JsonElement::class.java)
            gson.toJson(jsonElement, out)
        }

        override fun setIn(`in`: Reader) {
            this.`in` = `in`
        }

        override fun setOut(out: Writer) {
            this.out = out
        }
    }
}