package io.github.saneea.dvh.feature.binary.base64;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Base64;

import io.github.saneea.dvh.Feature;
import io.github.saneea.dvh.FeatureContext;

public class FromBase64 implements Feature, Feature.In.Bin.Stream, Feature.Out.Bin.Stream {

	private InputStream in;
	private OutputStream out;

	@Override
	public Meta meta(FeatureContext context) {
		return Meta.from("convert input Base64 sequence to binary");
	}

	@Override
	public void run(FeatureContext context) throws IOException {
		try (InputStream base64stream = Base64.getDecoder().wrap(in)) {
			base64stream.transferTo(out);
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
