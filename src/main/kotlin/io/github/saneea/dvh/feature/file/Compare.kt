package io.github.saneea.dvh.feature.file

import io.github.saneea.dvh.Feature
import io.github.saneea.dvh.StringConsumer
import java.io.File
import java.io.InputStream

class Compare :
    FileFeature(),
    Feature.In.Bin.Stream,
    Feature.Out.Text.String {

    override lateinit var inBinStream: InputStream
    override lateinit var outTextString: StringConsumer

    override val description = "compare file with standard input (full binary comparison)"

    override fun handleFile(file: File) {
        file.inputStream().buffered()
            .use {
                outTextString(
                    if (compareStreams(it, inBinStream))
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
}