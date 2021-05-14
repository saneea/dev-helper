package io.github.saneea.dvh.feature.text

import io.github.saneea.dvh.Feature
import io.github.saneea.dvh.Feature.Meta
import io.github.saneea.dvh.FeatureContext
import java.io.Reader
import java.io.Writer

class JoinLines :
    Feature,
    Feature.In.Text.Reader,
    Feature.Out.Text.Writer {

    private lateinit var `in`: Reader
    private lateinit var out: Writer

    override fun meta(context: FeatureContext) = Meta("join lines to one line")

    override fun run(context: FeatureContext) {
        `in`.buffered().use {
            it
                .lines()
                .map(String::trim)
                .forEach(out::write)
        }
    }

    override fun setIn(`in`: Reader) {
        this.`in` = `in`
    }

    override fun setOut(out: Writer) {
        this.out = out
    }
}