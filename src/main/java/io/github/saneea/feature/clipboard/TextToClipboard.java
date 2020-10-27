package io.github.saneea.feature.clipboard;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;

import io.github.saneea.Feature;
import io.github.saneea.FeatureContext;

public class TextToClipboard implements Feature, Feature.In.Text.String {

	private String in;

	@Override
	public String getShortDescription() {
		return "write text to clipboard";
	}

	@Override
	public void run(FeatureContext context) throws Exception {

		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

		StringSelection selection = new StringSelection(in);
		clipboard.setContents(selection, selection);
	}

	@Override
	public void setIn(String in) {
		this.in = in;
	}

}
