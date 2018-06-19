package io.github.saneea.feature;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import io.github.saneea.Feature;

public class XmlPrettyPrint implements Feature {

	@Override
	public void run(InputStream input, OutputStream output, String[] args) throws Exception {
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		transformerFactory.setAttribute("indent-number", 4);// pretty-print gap
		Transformer transformer = transformerFactory.newTransformer();

		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");// pretty-print

		Source source = new StreamSource(input);
		try (Writer writer = new OutputStreamWriter(output)) {
			Result result = new StreamResult(writer);
			transformer.transform(source, result);
		}
	}
}
