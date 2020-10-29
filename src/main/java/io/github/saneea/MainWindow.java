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

public class MainWindow extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final Clipboard CLIPBOARD = Toolkit.getDefaultToolkit().getSystemClipboard();

	private final List<TextField> outputTextFields = new ArrayList<>();

	public MainWindow() {
		Container contentPane = getContentPane();

		addInput(contentPane);
		addConverter(contentPane, "lower case", String::toLowerCase);
		addConverter(contentPane, "UPPER CASE", String::toUpperCase);
		addConverter(contentPane, "Char array", new String2CharArray());

		contentPane.setLayout(new GridLayout(outputTextFields.size() + 1, 3));

		pack();
	}

	private void addInput(Container contentPane) {
		JLabel label = new JLabel("Orig: ");

		TextField inputTextField = new TextField(5);
		inputTextField.addTextChangeListener(this::onInputTextChanged);

		JButton pasteFromClipboardButton = new JButton("Paste -->");
		pasteFromClipboardButton.addActionListener((actionEvent) -> {
			String text = readFromClipboard();
			if (text != null && !text.isEmpty()) {
				inputTextField.setText(text);
			}
		});

		contentPane.add(pasteFromClipboardButton);
		contentPane.add(label);
		contentPane.add(inputTextField);
	}

	private void addConverter(Container contentPane, String name, TextFunction textConverter) {
		JLabel label = new JLabel(name + ": ");

		TextField outputTextField = new TextField(5);
		outputTextField.setTextConverter(textConverter);

		JButton copyToClipboardButton = new JButton("<-- Copy");
		copyToClipboardButton.addActionListener((actionEvent) -> copyToClipboard(outputTextField.getText()));

		contentPane.add(copyToClipboardButton);
		contentPane.add(label);
		contentPane.add(outputTextField);

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
