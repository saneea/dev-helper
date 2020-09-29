package io.github.saneea.feature;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.Reader;
import java.io.StringWriter;

import io.github.saneea.Feature;
import io.github.saneea.FeatureContext;

public class TextToClipboard implements Feature, Feature.In.Text.Reader {

	private Reader in;

	@Override
	public String getShortDescription() {
		return "write text to clipboard";
	}

	@Override
	public void run(FeatureContext context) throws Exception {

		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

		StringWriter sw = new StringWriter();

		in.transferTo(sw);

		StringSelection selection = new StringSelection(sw.toString());
		clipboard.setContents(selection, selection);
	}

	@Override
	public void setReader(Reader in) {
		this.in = in;
	}

}
