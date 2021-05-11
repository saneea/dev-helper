package io.github.saneea.dvh.feature.file

import io.github.saneea.dvh.Feature
import io.github.saneea.dvh.StringConsumer
import java.io.File
import java.io.InputStream

class Compare :
    FileFeature(),
    Feature.In.Bin.Stream,
    Feature.Out.Text.String {

    private lateinit var `in`: InputStream
    private lateinit var out: StringConsumer

    override val description = "compare file with standard input (full binary comparison)"

    override fun handleFile(file: File) {
        file.inputStream().buffered()
            .use {
                out(
                    if (compareStreams(it, `in`))
                        "=="
                    else
                        "!="
                )
            }
    }

    private fun compareStreams(s1: InputStream, s2: InputStream): Boolean {
        while (true) {
            val b1 = s1.read()
            val b2 = s2.read()
            if (b1 != b2) {
                return false
            }
            if (b1 == -1) {
                return true
            }
        }
    }

    override fun setIn(`in`: InputStream) {
        this.`in` = `in`
    }

    override fun setOut(out: StringConsumer) {
        this.out = out
    }
}