package io.github.saneea.feature.clipboard;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;

import io.github.saneea.Feature;
import io.github.saneea.Feature.Util.IOConsumer;
import io.github.saneea.FeatureContext;

public class TextFromClipboard implements Feature, Feature.Out.Text.String {

	private IOConsumer<String> out;

	@Override
	public String getShortDescription() {
		return "read text from clipboard";
	}

	@Override
	public void run(FeatureContext context) throws Exception {

		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

		String clipboardText = (String) clipboard.getData(DataFlavor.stringFlavor);

		out.accept(clipboardText);
	}

	@Override
	public void setOut(IOConsumer<String> out) {
		this.out = out;
	}

}
