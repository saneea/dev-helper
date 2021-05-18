package io.github.saneea.dvh.feature.file

import io.github.saneea.dvh.Feature
import java.io.File
import java.io.InputStream

class WriteFile :
    FileFeature(),
    Feature.In.Bin.Stream {

    override lateinit var inBinStream: InputStream

    override val description = "transfer bytes from standard input to file"

    override fun handleFile(file: File) {
        file
            .outputStream().buffered()
            .use(inBinStream::transferTo)
    }
}