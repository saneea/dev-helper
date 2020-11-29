package io.github.saneea.feature.binary.base64;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Base64;

import io.github.saneea.Feature;
import io.github.saneea.FeatureContext;

public class ToBase64 implements Feature, Feature.In.Bin.Stream, Feature.Out.Bin.Stream {

	private InputStream in;
	private OutputStream out;

	@Override
	public Meta meta(FeatureContext context) {
		return Meta.from("convert input binary sequence to Base64");
	}

	@Override
	public void run(FeatureContext context) throws IOException {
		try (OutputStream base64stream = Base64.getEncoder().wrap(out)) {
			in.transferTo(base64stream);
		}
	}

	@Override
	public void setIn(InputStream in) {
		this.in = in;
	}

	@Override
	public void setOut(OutputStream out) {
		this.out = out;
	}

}
