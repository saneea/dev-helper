package io.github.saneea.feature;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringWriter;

import io.github.saneea.Feature;

public class TextToClipboard implements Feature {

	@Override
	public void run(InputStream input, OutputStream output, String[] args) throws Exception {

		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

		StringWriter sw = new StringWriter();

		try (Reader reader = new InputStreamReader(input)) {
			reader.transferTo(sw);
		}

		StringSelection selection = new StringSelection(sw.toString());
		clipboard.setContents(selection, selection);
	}

}
