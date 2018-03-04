package io.github.saneea;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import javax.swing.JTextField;

import io.github.saneea.textfunction.TextFunction;

public class TextField extends JTextField {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private TextFunction textConverter;

	private final List<Consumer<String>> listeners = new ArrayList<>();

	private String currentValue;

	public TextField(int columns) {
		super(columns);

		addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
				onTextChanged();
			}

			@Override
			public void keyReleased(KeyEvent e) {
				onTextChanged();
			}

			@Override
			public void keyPressed(KeyEvent e) {
				onTextChanged();
			}
		});
	}

	private void onTextChanged() {
		String newValue = getText();
		if (newValue != null && !newValue.equals(currentValue)) {
			currentValue = newValue;
			for (Consumer<String> listener : listeners) {
				listener.accept(newValue);
			}
		}
	}

	public void addTextChangeListener(Consumer<String> listener) {
		listeners.add(listener);
	}

	@Override
	public void setText(String text) {
		super.setText(prepareText(text));
		onTextChanged();
	}

	private String prepareText(String text) {
		return textConverter != null ? textConverter.apply(text) : text;
	}

	public void setTextConverter(TextFunction textConverter) {
		this.textConverter = textConverter;
	}

}
