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
import java.io.PipedReader;
import java.io.PipedWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

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
import io.github.saneea.FeatureContext;

public class XmlReform implements Feature {

	@Override
	public String getShortDescription() {
		return "remove whitespaces from XML and then format XML with indents";
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
				onAttributes(attributes);
			} catch (XMLStreamException e) {
				throw new SAXException(e);
			}
		}

		private void onAttributes(Attributes attributes) throws XMLStreamException {
			for (Entry<String, String> entry : asMap(attributes).entrySet()) {
				xWriter.writeAttribute(entry.getKey(), entry.getValue());
			}
		}

		private static Map<String, String> asMap(Attributes attributes) {
			Map<String, String> ret = new TreeMap<>();
			for (int i = 0, count = attributes.getLength(); i < count; ++i) {
				ret.put(attributes.getLocalName(i), attributes.getValue(i));
			}
			return ret;
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

		private final Reader inputReader;
		private final Writer outputWriter;

		private TransformerException exception;

		public BackgroundTransformerThread(Reader inputReader, Writer outputWriter) throws IOException {
			this.inputReader = inputReader;
			this.outputWriter = outputWriter;
		}

		@Override
		public void run() {
			try {
				transform(new StreamSource(inputReader), new StreamResult(outputWriter));
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

		try (PipedReader pipedReader = new PipedReader()) {
			BackgroundTransformerThread backgroundTransformerThread = //
					new BackgroundTransformerThread(pipedReader, outputWriter);
			try (Writer pipedWriter = new PipedWriter(pipedReader); //
					XmlHandler xmlHandler = new XmlHandler(pipedWriter)) {

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
	public void run(FeatureContext context) throws Exception {
		Options options = Params.createOptions();

		CommandLineParser commandLineParser = new DefaultParser();
		CommandLine commandLine = commandLineParser.parse(options, context.getArgs());

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
