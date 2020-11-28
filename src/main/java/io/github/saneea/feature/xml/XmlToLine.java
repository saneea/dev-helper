package io.github.saneea.feature.xml;

import java.io.Reader;
import java.io.Writer;
import java.util.ArrayDeque;
import java.util.Deque;

import javax.xml.parsers.SAXParserFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import io.github.saneea.Feature;
import io.github.saneea.FeatureContext;

public class XmlToLine implements Feature, Feature.In.Text.Reader, Feature.Out.Text.Writer {

	private Reader in;
	private Writer out;

	@Override
	public Meta meta(FeatureContext context) {
		return Meta.from("remove whitespaces from XML");
	}

	@Override
	public void run(FeatureContext context) throws Exception {
		transform(in, out);
	}

	public static void transform(Reader in, Writer out) throws Exception {
		try (XmlHandler xmlHandler = new XmlHandler(out)) {
			SAXParserFactory//
					.newInstance()//
					.newSAXParser()//
					.parse(new InputSource(in), xmlHandler);
		}
	}

	@Override
	public void setIn(Reader in) {
		this.in = in;
	}

	@Override
	public void setOut(Writer out) {
		this.out = out;
	}

	private static class XmlHandler extends DefaultHandler implements AutoCloseable {

		private static class ElementFrame {
			boolean hasChildrenElements;

			public boolean isHasChildrenElements() {
				return hasChildrenElements;
			}

			public void setHasChildrenElements(boolean hasChildrenElements) {
				this.hasChildrenElements = hasChildrenElements;
			}

		}

		private final XMLStreamWriter xWriter;

		private Deque<ElementFrame> contentStack = new ArrayDeque<>();

		private final StringBuilder currentContent = new StringBuilder();

		public XmlHandler(Writer writer) throws XMLStreamException {
			xWriter = XMLOutputFactory//
					.newFactory()//
					.createXMLStreamWriter(writer);

			contentStack.push(new ElementFrame());// root
		}

		@Override
		public void startElement(String uri, String localName, String qName, Attributes attributes)
				throws SAXException {
			contentStack.peekLast().setHasChildrenElements(true);
			contentStack.addLast(new ElementFrame());

			try {
				String contentBefore = withdrawContentBefore();
				if (!contentBefore.trim().isEmpty()) {
					xWriter.writeCharacters(contentBefore);
				}
				xWriter.writeStartElement(qName);// TODO use method writeStartElement with nameSpace and other
				for (int i = 0, count = attributes.getLength(); i < count; ++i) {
					xWriter.writeAttribute(attributes.getLocalName(i), attributes.getValue(i));
				}
			} catch (XMLStreamException e) {
				throw new SAXException(e);
			}
		}

		@Override
		public void endElement(String uri, String localName, String qName) throws SAXException {
			String contentBefore = withdrawContentBefore();
			ElementFrame currentFrame = contentStack.pollLast();
			try {
				if (!currentFrame.isHasChildrenElements() || !contentBefore.trim().isEmpty()) {
					xWriter.writeCharacters(contentBefore);
				}
				xWriter.writeEndElement();
			} catch (XMLStreamException e) {
				throw new SAXException(e);
			}
		}

		@Override
		public void characters(char[] ch, int start, int length) throws SAXException {
			currentContent.append(ch, start, length);
		}

		private String withdrawContentBefore() {
			String contentBefore = currentContent.toString();
			currentContent.delete(0, currentContent.length());
			return contentBefore;
		}

		@Override
		public void close() throws XMLStreamException {
			xWriter.close();
		}

	}

}
