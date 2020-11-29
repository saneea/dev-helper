package io.github.saneea.feature.xml;

import java.io.Reader;
import java.io.Writer;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

import io.github.saneea.Feature;
import io.github.saneea.FeatureContext;
import io.github.saneea.utils.ReaderFromWriter;

public class XmlPrettyPrint implements //
		Feature, //
		Feature.CLI, //
		Feature.CLI.Options, //
		Feature.In.Text.Reader, //
		Feature.Out.Text.Writer {

	private static final String NON_TO_LINE_BEFORE = "nonToLineBefore";

	private Reader in;
	private Writer out;
	private CommandLine commandLine;

	@Override
	public Meta meta(FeatureContext context) {
		return Meta.from("format XML with indents");
	}

	@Override
	public void run(FeatureContext context) throws Exception {
		run(//
				in, //
				out, //
				commandLine.hasOption(NON_TO_LINE_BEFORE));
	}

	public static void run(Reader in, Writer out, boolean nonToLineBefore) throws Exception {
		Reader indentsIn = nonToLineBefore//
				? in //
				: new ReaderFromWriter(//
						toLineOut -> XmlToLine.transform(in, toLineOut));

		transform(indentsIn, out);
	}

	private static void transform(Reader in, Writer out) throws Exception {
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

	@Override
	public void setCommandLine(CommandLine commandLine) {
		this.commandLine = commandLine;
	}

	@Override
	public Option[] getOptions() {
		Option[] options = { //
				Option//
						.builder("ntlb")//
						.longOpt(NON_TO_LINE_BEFORE)//
						.desc("do not remove whitespaces between tags before add indents")//
						.build()//
		};

		return options;
	}
}
