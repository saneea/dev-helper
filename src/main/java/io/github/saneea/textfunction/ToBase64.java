package io.github.saneea.textfunction;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Base64;

import io.github.saneea.Feature;
import io.github.saneea.FeatureContext;

public class ToBase64 implements Feature {

	@Override
	public String getShortDescription() {
		return "convert input binary sequence to Base64";
	}

	@Override
	public void run(FeatureContext context) throws IOException {
		try (OutputStream base64stream = Base64.getEncoder().wrap(context.getOut())) {
			context.getIn().transferTo(base64stream);
		}
	}

}
