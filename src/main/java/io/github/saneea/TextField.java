package io.github.saneea;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.function.Consumer;

import javax.swing.JTextField;

import io.github.saneea.textfunction.TextFunction;

public class TextField extends JTextField {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private TextFunction textConverter;

	public TextField(int columns) {
		super(columns);
	}

	public void addTextChangeListener(Consumer<String> listener) {
		addKeyListener(new KeyListener() {

			private String currentValue;

			private void onKey() {
				String newValue = getText();

				if (newValue != null && !newValue.equals(currentValue)) {
					currentValue = newValue;
					listener.accept(currentValue);
				}
			}

			@Override
			public void keyTyped(KeyEvent e) {
				onKey();
			}

			@Override
			public void keyReleased(KeyEvent e) {
				onKey();
			}

			@Override
			public void keyPressed(KeyEvent e) {
				onKey();
			}
		});
	}

	@Override
	public void setText(String text) {
		super.setText(prepareText(text));
	}

	private String prepareText(String text) {
		return textConverter != null ? textConverter.apply(text) : text;
	}

	public void setTextConverter(TextFunction textConverter) {
		this.textConverter = textConverter;
	}

}
