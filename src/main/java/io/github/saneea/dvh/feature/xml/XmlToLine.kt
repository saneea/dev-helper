package io.github.saneea.dvh.feature.xml

import io.github.saneea.dvh.Feature
import io.github.saneea.dvh.Feature.Meta
import io.github.saneea.dvh.FeatureContext
import org.xml.sax.Attributes
import org.xml.sax.InputSource
import org.xml.sax.helpers.DefaultHandler
import java.io.Closeable
import java.io.Reader
import java.io.Writer
import java.util.*
import javax.xml.parsers.SAXParserFactory
import javax.xml.stream.XMLOutputFactory
import javax.xml.stream.XMLStreamWriter

class XmlToLine :
    Feature,
    Feature.In.Text.Reader,
    Feature.Out.Text.Writer {

    private lateinit var `in`: Reader
    private lateinit var out: Writer

    override fun meta(context: FeatureContext) = Meta("format XML to line")

    override fun run(context: FeatureContext) =
        transform(`in`, out)

    override fun setIn(`in`: Reader) {
        this.`in` = `in`
    }

    override fun setOut(out: Writer) {
        this.out = out
    }

    private class XmlHandler(writer: Writer) : DefaultHandler(), Closeable {

        private class ElementFrame {
            var isHasChildrenElements = false
        }

        private val xWriter: XMLStreamWriter =
            XMLOutputFactory.newFactory().createXMLStreamWriter(writer)

        private val contentStack: Deque<ElementFrame> = ArrayDeque()
        private val currentContent = StringBuilder()

        init {
            contentStack.push(ElementFrame()) // root
        }

        override fun startElement(uri: String, localName: String, qName: String, attributes: Attributes) {
            contentStack.peekLast().isHasChildrenElements = true
            contentStack.addLast(ElementFrame())

            val contentBefore = withdrawContentBefore()
            if (contentBefore.isNotBlank()) {
                xWriter.writeCharacters(contentBefore)
            }
            xWriter.writeStartElement(qName) // TODO use method writeStartElement with nameSpace and other

            for (i in 0 until attributes.length) {
                xWriter.writeAttribute(attributes.getLocalName(i), attributes.getValue(i))
            }
        }

        override fun endElement(uri: String, localName: String, qName: String) {
            val contentBefore = withdrawContentBefore()
            val currentFrame = contentStack.pollLast()

            if (!currentFrame.isHasChildrenElements || contentBefore.isNotBlank()) {
                xWriter.writeCharacters(contentBefore)
            }
            xWriter.writeEndElement()
        }

        override fun characters(ch: CharArray, start: Int, length: Int) {
            currentContent.append(ch, start, length)
        }

        private fun withdrawContentBefore(): String {
            val contentBefore = currentContent.toString()
            currentContent.clear()
            return contentBefore
        }

        override fun close() =
            xWriter.close()
    }

    companion object {

        fun transform(`in`: Reader, out: Writer) =
            XmlHandler(out).use { xmlHandler ->
                SAXParserFactory.newInstance().newSAXParser()
                    .parse(InputSource(`in`), xmlHandler)
            }
    }
}