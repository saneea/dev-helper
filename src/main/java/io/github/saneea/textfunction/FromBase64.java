package io.github.saneea.textfunction;

import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;

import io.github.saneea.Feature;
import io.github.saneea.FeatureContext;

public class FromBase64 implements Feature {

	@Override
	public void run(FeatureContext context) throws IOException {
		try (InputStream base64stream = Base64.getDecoder().wrap(context.getIn())) {
			base64stream.transferTo(context.getOut());
		}
	}

}
