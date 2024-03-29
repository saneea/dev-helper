package io.github.saneea.dvh.feature.xml

import io.github.saneea.dvh.Feature
import io.github.saneea.dvh.Feature.Meta
import io.github.saneea.dvh.utils.ReaderFromWriter
import org.apache.commons.cli.CommandLine
import org.apache.commons.cli.Option
import java.io.Reader
import java.io.Writer
import javax.xml.transform.OutputKeys
import javax.xml.transform.Result
import javax.xml.transform.Source
import javax.xml.transform.TransformerFactory
import javax.xml.transform.stream.StreamResult
import javax.xml.transform.stream.StreamSource

private const val NON_TO_LINE_BEFORE = "nonToLineBefore"

private fun transform(`in`: Reader, out: Writer) {
    val transformerFactory = TransformerFactory.newInstance()
    transformerFactory.setAttribute("indent-number", 4) // pretty-print gap
    val transformer = transformerFactory.newTransformer()
    transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes")
    transformer.setOutputProperty(OutputKeys.INDENT, "yes") // pretty-print
    val source: Source = StreamSource(`in`)
    val result: Result = StreamResult(out)
    transformer.transform(source, result)
}

class XmlPrettyPrint :
    Feature,
    Feature.CLI,
    Feature.CLI.Options,
    Feature.In.Text.Reader,
    Feature.Out.Text.Writer {

    override lateinit var inTextReader: Reader
    override lateinit var outTextWriter: Writer
    override lateinit var commandLine: CommandLine

    override val meta = Meta("format XML with indents")

    override fun run() =
        run(inTextReader, outTextWriter, commandLine.hasOption(NON_TO_LINE_BEFORE))

    override val options = listOf(
        Option
            .builder("ntlb")
            .longOpt(NON_TO_LINE_BEFORE)
            .desc("do not remove whitespaces between tags before add indents")
            .build()
    )

    companion object {

        fun run(`in`: Reader, out: Writer, nonToLineBefore: Boolean) {
            val indentsIn = if (nonToLineBefore)
                `in`
            else
                ReaderFromWriter { toLineOut -> XmlToLine.transform(`in`, toLineOut) }

            transform(indentsIn, out)
        }

    }
}