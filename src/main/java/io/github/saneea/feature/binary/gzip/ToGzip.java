package io.github.saneea.feature.binary.gzip;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPOutputStream;

import io.github.saneea.Feature;
import io.github.saneea.FeatureContext;

public class ToGzip implements//
		Feature, //
		Feature.In.Bin.Stream, //
		Feature.Out.Bin.Stream {

	private InputStream in;
	private OutputStream out;

	@Override
	public Meta meta(FeatureContext context) {
		return Meta.from("compress to GZIP");
	}

	@Override
	public void run(FeatureContext context) throws IOException {
		try (GZIPOutputStream gzipOut = new GZIPOutputStream(out)) {
			in.transferTo(gzipOut);
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