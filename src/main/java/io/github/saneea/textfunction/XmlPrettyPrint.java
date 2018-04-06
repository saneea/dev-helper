package io.github.saneea.textfunction;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.Deque;

import javax.xml.parsers.SAXParserFactory;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class XmlPrettyPrint {

	private static class XmlHandler extends DefaultHandler {

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

		public XmlHandler(OutputStream outputStream) throws XMLStreamException, FactoryConfigurationError {
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
				xWriter.writeCharacters(withdrawContentBefore().trim());
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

	}

	private static void transformInBackground(PipedOutputStream in, String fileName) {
		new Thread(() -> {

			try (InputStream inputStream = new PipedInputStream(in); //
					OutputStreamWriter outputStream = new OutputStreamWriter(//
							new BufferedOutputStream(//
									new FileOutputStream(fileName))//
			, StandardCharsets.UTF_8)) {
				transform(new StreamSource(inputStream), new StreamResult(outputStream));
			} catch (IOException | TransformerException e) {
				throw new RuntimeException(e);
			}

		}).start();
	}

	private static void transform(Source source, Result result) throws TransformerException {
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		transformerFactory.setAttribute("indent-number", 4);// pretty-print gap
		Transformer transformer = transformerFactory.newTransformer();

		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");// pretty-print

		transformer.transform(source, result);
	}

	public static void main(String[] args) throws Exception {

		String inputFileName = args[0];
		String outputFileName = args[1];

		try (PipedOutputStream pipedOutputStream = new PipedOutputStream()) {
			transformInBackground(pipedOutputStream, outputFileName);
			SAXParserFactory//
					.newInstance()//
					.newSAXParser()//
					.parse(inputFileName, new XmlHandler(pipedOutputStream));
		}
	}
}
