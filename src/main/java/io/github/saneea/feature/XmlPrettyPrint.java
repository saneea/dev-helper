package io.github.saneea.feature;

import java.io.Reader;
import java.io.Writer;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import io.github.saneea.Feature;
import io.github.saneea.FeatureContext;

public class XmlPrettyPrint implements Feature, Feature.In.Text.Reader, Feature.Out.Text.Writer {

	private Reader in;
	private Writer out;

	@Override
	public String getShortDescription() {
		return "format XML with indents";
	}

	@Override
	public void run(FeatureContext context) throws Exception {
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		transformerFactory.setAttribute("indent-number", 4);// pretty-print gap
		Transformer transformer = transformerFactory.newTransformer();

		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");// pretty-print

		Source source = new StreamSource(in);
		Result result = new StreamResult(out);
		transformer.transform(source, result);
	}

	@Override
	public void setIn(Reader in) {
		this.in = in;
	}

	@Override
	public void setOut(Writer out) {
		this.out = out;
	}
}
