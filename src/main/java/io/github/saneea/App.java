package io.github.saneea;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**
 * Hello world!
 *
 */
public class App {

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			MainWindow mainWindow = new MainWindow();
			mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			mainWindow.setVisible(true);
		});
	}

}
