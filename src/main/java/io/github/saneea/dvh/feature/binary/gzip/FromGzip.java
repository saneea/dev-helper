package io.github.saneea.dvh.feature.binary.gzip;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;

import io.github.saneea.dvh.Feature;
import io.github.saneea.dvh.FeatureContext;

public class FromGzip implements//
		Feature, //
		Feature.In.Bin.Stream, //
		Feature.Out.Bin.Stream {

	private InputStream in;
	private OutputStream out;

	@Override
	public Meta meta(FeatureContext context) {
		return Meta.from("extract from GZIP");
	}

	@Override
	public void run(FeatureContext context) throws IOException {
		try (GZIPInputStream gzipIn = new GZIPInputStream(in)) {
			gzipIn.transferTo(out);
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