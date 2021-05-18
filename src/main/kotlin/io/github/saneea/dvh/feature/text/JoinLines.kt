package io.github.saneea.dvh.feature.text

import io.github.saneea.dvh.Feature
import io.github.saneea.dvh.Feature.Meta
import java.io.Reader
import java.io.Writer

class JoinLines :
    Feature,
    Feature.In.Text.Reader,
    Feature.Out.Text.Writer {
    
    override lateinit var inTextReader: Reader
    override lateinit var outTextWriter: Writer

    override val meta = Meta("join lines to one line")

    override fun run() {
        inTextReader.buffered().use {
            it
                .lines()
                .map(String::trim)
                .forEach(outTextWriter::write)
        }
    }
}