package io.github.saneea.feature;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;

import io.github.saneea.Feature;
import io.github.saneea.FeatureContext;

public class TextToClipboard implements Feature {

	@Override
	public String getShortDescription() {
		return "write text to clipboard";
	}

	@Override
	public void run(FeatureContext context) throws Exception {

		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

		StringWriter sw = new StringWriter();

		try (Reader reader = new InputStreamReader(context.getIn())) {
			reader.transferTo(sw);
		}

		StringSelection selection = new StringSelection(sw.toString());
		clipboard.setContents(selection, selection);
	}

}
