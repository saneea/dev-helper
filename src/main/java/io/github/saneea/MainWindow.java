package io.github.saneea;

import java.awt.Container;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

import io.github.saneea.textfunction.TextFunction;
import io.github.saneea.textfunction.UnixPathNormalizer;
import io.github.saneea.textfunction.WindowsPathNormalizer;

public class MainWindow extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final TextField inputTextField = new TextField(5);

	private final List<TextField> outputTextFields = new ArrayList<>();

	public MainWindow() {
		Container contentPane = getContentPane();

		contentPane.setLayout(new GridLayout(3, 3));

		contentPane.add(new JLabel("Orig: "));
		inputTextField.addTextChangeListener(this::onInputTextChanged);
		contentPane.add(inputTextField);
		contentPane.add(new JButton("<-- Paste from clipboard"));

		addConverter(contentPane, "Windows path", new WindowsPathNormalizer());
		addConverter(contentPane, "Unix path", new UnixPathNormalizer());

		pack();
	}

	private void addConverter(Container contentPane, String name, TextFunction textConverter) {
		contentPane.add(new JLabel(name + ": "));
		TextField outputTextField = new TextField(5);
		outputTextField.setTextConverter(textConverter);
		contentPane.add(outputTextField);
		contentPane.add(new JButton("--> Copy to clipboard"));
		outputTextFields.add(outputTextField);
	}

	private void onInputTextChanged(String input) {
		for (TextField outputTextField : outputTextFields) {
			outputTextField.setText(input);
		}
	}
}
