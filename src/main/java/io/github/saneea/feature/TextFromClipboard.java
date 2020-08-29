package io.github.saneea.feature;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import io.github.saneea.Feature;

public class TextFromClipboard implements Feature {

	@Override
	public void run(InputStream input, OutputStream output, String[] args) throws Exception {

		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

		String clipboardText = (String) clipboard.getData(DataFlavor.stringFlavor);

		try (OutputStreamWriter writer = new OutputStreamWriter(output)) {
			writer.write(clipboardText);
		}

	}

}
