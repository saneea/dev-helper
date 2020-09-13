package io.github.saneea.feature;

import java.io.OutputStream;
import java.util.ArrayDeque;
import java.util.Deque;

import javax.xml.parsers.SAXParserFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import io.github.saneea.Feature;
import io.github.saneea.FeatureContext;

public class XmlToLine implements Feature {

	@Override
	public String getShortDescription() {
		return "remove whitespaces from XML";
	}

	@Override
	public void run(FeatureContext context) throws Exception {
		try (XmlHandler xmlHandler = new XmlHandler(context.getOut())) {
			SAXParserFactory//
					.newInstance()//
					.newSAXParser()//
					.parse(context.getIn(), xmlHandler);
		}
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

		public XmlHandler(OutputStream outputStream) throws XMLStreamException {
			xWriter = XMLOutputFactory//
					.newFactory()//
					.createXMLStreamWriter(outputStream);

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
