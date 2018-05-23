package io.github.saneea;

import java.awt.Container;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

import io.github.saneea.textfunction.String2CharArray;
import io.github.saneea.textfunction.TextFunction;
import io.github.saneea.textfunction.UnixPathNormalizer;
import io.github.saneea.textfunction.WindowsPathNormalizer;

public class MainWindow extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final Clipboard CLIPBOARD = Toolkit.getDefaultToolkit().getSystemClipboard();

	private final TextField inputTextField = new TextField(5);

	private final List<TextField> outputTextFields = new ArrayList<>();

	public MainWindow() {
		Container contentPane = getContentPane();

		contentPane.setLayout(new GridLayout(6, 3));

		contentPane.add(new JLabel("Orig: "));
		inputTextField.addTextChangeListener(this::onInputTextChanged);
		contentPane.add(inputTextField);
		JButton pasteFromClipboardButton = new JButton("<-- Paste from clipboard");
		pasteFromClipboardButton.addActionListener((actionEvent) -> {
			String text = readFromClipboard();
			if (text != null && !text.isEmpty()) {
				inputTextField.setText(text);
			}
		});
		contentPane.add(pasteFromClipboardButton);

		addConverter(contentPane, "Windows path", new WindowsPathNormalizer());
		addConverter(contentPane, "Unix path", new UnixPathNormalizer());
		addConverter(contentPane, "lower case", String::toLowerCase);
		addConverter(contentPane, "UPPER CASE", String::toUpperCase);
		addConverter(contentPane, "Char array", new String2CharArray());

		pack();
	}

	private void addConverter(Container contentPane, String name, TextFunction textConverter) {
		contentPane.add(new JLabel(name + ": "));
		TextField outputTextField = new TextField(5);
		outputTextField.setTextConverter(textConverter);
		contentPane.add(outputTextField);
		JButton copyToClipboardButton = new JButton("--> Copy to clipboard");
		copyToClipboardButton.addActionListener((actionEvent) -> copyToClipboard(outputTextField.getText()));
		contentPane.add(copyToClipboardButton);
		outputTextFields.add(outputTextField);
	}

	private void copyToClipboard(String text) {
		StringSelection selection = new StringSelection(text);
		CLIPBOARD.setContents(selection, selection);
	}

	private String readFromClipboard() {
		try {
			return (String) CLIPBOARD.getData(DataFlavor.stringFlavor);
		} catch (Exception e) {
			return null;
		}
	}

	private void onInputTextChanged(String input) {
		for (TextField outputTextField : outputTextFields) {
			outputTextField.setText(input);
		}
	}
}
