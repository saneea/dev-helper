package io.github.saneea.feature;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayDeque;
import java.util.Deque;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
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

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import io.github.saneea.Feature;

public class XmlReform implements Feature {

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

	private static class BackgroundTransformerThread extends Thread {

		private final InputStream inputStream;
		private final Writer outputWriter;

		private TransformerException exception;

		public BackgroundTransformerThread(InputStream inputStream, Writer outputWriter) throws IOException {
			this.inputStream = inputStream;
			this.outputWriter = outputWriter;
		}

		@Override
		public void run() {
			try {
				transform(new StreamSource(inputStream), new StreamResult(outputWriter));
			} catch (TransformerException e) {
				exception = e;
			}
		}

	}

	private static void transform(Source source, Result result) throws TransformerException {
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		transformerFactory.setAttribute("indent-number", 4);// pretty-print gap
		Transformer transformer = transformerFactory.newTransformer();

		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");// pretty-print

		transformer.transform(source, result);
	}

	public static void execute(InputStream inputStream, Writer outputWriter) throws IOException, SAXException,
			ParserConfigurationException, XMLStreamException, InterruptedException, TransformerException {

		try (PipedInputStream pipedInputStream = new PipedInputStream()) {
			BackgroundTransformerThread backgroundTransformerThread = //
					new BackgroundTransformerThread(pipedInputStream, outputWriter);
			try (Writer pipedOutputStreamWriter = new OutputStreamWriter(//
					new PipedOutputStream(pipedInputStream), StandardCharsets.UTF_8.name()); //
					XmlHandler xmlHandler = new XmlHandler(pipedOutputStreamWriter)) {

				backgroundTransformerThread.start();

				SAXParserFactory//
						.newInstance()//
						.newSAXParser()//
						.parse(inputStream, xmlHandler);
			}

			backgroundTransformerThread.join();

			if (backgroundTransformerThread.exception != null) {
				throw backgroundTransformerThread.exception;
			}
		}
	}

	public static void execute(String inputFileName, String outputFileName, String outputEncoding) throws IOException,
			SAXException, ParserConfigurationException, XMLStreamException, InterruptedException, TransformerException {
		File tmpOutFile = File.createTempFile("XmlPrettyPrint", "xml");
		tmpOutFile.deleteOnExit();

		try (InputStream inputStream = new BufferedInputStream(new FileInputStream(inputFileName)); //
				Writer outputWriter = new OutputStreamWriter(new BufferedOutputStream(//
						new FileOutputStream(tmpOutFile))//
						, outputEncoding)) {
			execute(inputStream, outputWriter);
		}

		try (OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(outputFileName))) {
			Files.copy(tmpOutFile.toPath(), outputStream);
		}
	}

	public static void main(String[] args) throws Exception {

		if (args.length != 2) {
			throw new Exception(//
					XmlReform.class.getSimpleName() + " usage: " + //
							XmlReform.class.getSimpleName() + " <input/file.name> <out/put.filename>");
		}

		execute(args[0], args[1], StandardCharsets.UTF_8.name());
	}

	@Override
	public void run(InputStream input, OutputStream output, String[] args) throws Exception {
		Options options = Params.createOptions();

		CommandLineParser commandLineParser = new DefaultParser();
		CommandLine commandLine = commandLineParser.parse(options, args);

		String inputFileName = commandLine.getOptionValue(Params.INPUT);
		String outputFileName = commandLine.getOptionValue(Params.OUTPUT);
		String outputEncoding = commandLine.getOptionValue(Params.OUTPUT_ENCODING, StandardCharsets.UTF_8.name());

		execute(inputFileName, outputFileName, outputEncoding);
	}

	public static class Params {

		public static String INPUT = "input";
		public static String OUTPUT = "output";
		public static String OUTPUT_ENCODING = "outputEncoding";

		private static Options createOptions() {
			Options options = new Options()//
					.addOption(Option//
							.builder("i")//
							.longOpt(INPUT)//
							.hasArg(true)//
							.argName("file path")//
							.required(true)//
							.desc("input file path")//
							.build())//
					.addOption(Option//
							.builder("o")//
							.longOpt(OUTPUT)//
							.hasArg(true)//
							.argName("file path")//
							.required(true)//
							.desc("output file path")//
							.build())//
					.addOption(Option//
							.builder("oe")//
							.longOpt(OUTPUT_ENCODING)//
							.hasArg(true)//
							.argName("encoding")//
							.required(false)//
							.desc("output encoding")//
							.build());

			return options;
		}
	}
}
