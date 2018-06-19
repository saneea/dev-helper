package io.github.saneea.feature;

import java.io.InputStream;
import java.io.OutputStream;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import io.github.saneea.Feature;
import io.github.saneea.MainWindow;

public class Gui implements Feature {

	@Override
	public void run(InputStream input, OutputStream output, String[] args) throws Exception {
		SwingUtilities.invokeLater(() -> {
			MainWindow mainWindow = new MainWindow();
			mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			mainWindow.setVisible(true);
		});
	}

}
