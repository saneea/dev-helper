package io.github.saneea.dvh.feature.file

import io.github.saneea.dvh.Feature
import io.github.saneea.dvh.utils.transferFrom
import java.io.File
import java.io.OutputStream

class ReadFile :
    FileFeature(),
    Feature.Out.Bin.Stream {

    private lateinit var out: OutputStream

    override val description = "transfer bytes from file to standard output"

    override fun handleFile(file: File) {
        file
            .inputStream().buffered()
            .use(out::transferFrom)
    }

    override fun setOutBinStream(out: OutputStream) {
        this.out = out
    }
}

