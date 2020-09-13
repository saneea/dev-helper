package io.github.saneea.feature;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.io.OutputStreamWriter;

import io.github.saneea.Feature;
import io.github.saneea.FeatureContext;

public class TextFromClipboard implements Feature {

	@Override
	public String getShortDescription() {
		return "read text from clipboard";
	}

	@Override
	public void run(FeatureContext context) throws Exception {

		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

		String clipboardText = (String) clipboard.getData(DataFlavor.stringFlavor);

		try (OutputStreamWriter writer = new OutputStreamWriter(context.getOut())) {
			writer.write(clipboardText);
		}

	}

}
