package io.github.saneea.feature;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import io.github.saneea.Feature;
import io.github.saneea.FeatureContext;
import io.github.saneea.MainWindow;

public class Gui implements Feature {

	@Override
	public String getShortDescription() {
		return "gui window for some text conversions";
	}

	@Override
	public void run(FeatureContext context) throws Exception {
		SwingUtilities.invokeLater(() -> {
			MainWindow mainWindow = new MainWindow();
			mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			mainWindow.setVisible(true);
		});
	}

}
