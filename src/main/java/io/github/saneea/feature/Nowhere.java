package io.github.saneea.feature;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.NoSuchAlgorithmException;

import io.github.saneea.Feature;
import io.github.saneea.FeatureContext;

public class Nowhere implements Feature, Feature.In.Bin.Stream {

	private InputStream in;

	@Override
	public Meta meta(FeatureContext context) {
		return Meta.from("read all input data and do nothing");
	}

	@Override
	public void run(FeatureContext context) throws NoSuchAlgorithmException, IOException {
		in.transferTo(OutputStream.nullOutputStream());
	}

	@Override
	public void setIn(InputStream in) {
		this.in = in;
	}
}
