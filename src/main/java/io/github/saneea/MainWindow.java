package io.github.saneea;

import java.awt.Container;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

import io.github.saneea.textfunction.UnixPathNormalizer;
import io.github.saneea.textfunction.WindowsPathNormalizer;

public class MainWindow extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	TextField inputTextField = new TextField(5);

	TextField outputTextFieldWindowsPath = new TextField(5);

	TextField outputTextFieldUnixPath = new TextField(5);

	public MainWindow() {
		Container contentPane = getContentPane();

		contentPane.setLayout(new GridLayout(3, 3));

		contentPane.add(new JLabel("Orig: "));
		inputTextField.addTextChangeListener(this::onInputTextChanged);
		contentPane.add(inputTextField);
		contentPane.add(new JButton("<-- Paste from clipboard"));

		contentPane.add(new JLabel("Windows path: "));
		outputTextFieldWindowsPath.setTextConverter(new WindowsPathNormalizer());
		contentPane.add(outputTextFieldWindowsPath);
		contentPane.add(new JButton("--> Copy to clipboard"));

		contentPane.add(new JLabel("Unix path: "));
		outputTextFieldUnixPath.setTextConverter(new UnixPathNormalizer());
		contentPane.add(outputTextFieldUnixPath);
		contentPane.add(new JButton("--> Copy to clipboard"));

		pack();
	}

	private void onInputTextChanged(String input) {
		outputTextFieldWindowsPath.setText(input);
		outputTextFieldUnixPath.setText(input);
	}
}
