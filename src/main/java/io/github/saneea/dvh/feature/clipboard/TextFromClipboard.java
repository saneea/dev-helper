package io.github.saneea.dvh.feature.clipboard;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;

import io.github.saneea.dvh.Feature;
import io.github.saneea.dvh.Feature.Util.IOConsumer;
import io.github.saneea.dvh.FeatureContext;

public class TextFromClipboard implements Feature, Feature.Out.Text.String {

	private IOConsumer<String> out;

	@Override
	public Meta meta(FeatureContext context) {
		return Meta.from("read text from clipboard");
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
