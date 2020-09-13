package io.github.saneea;

import java.io.InputStream;
import java.io.OutputStream;

public class FeatureContext {
	final String[] args;
	final InputStream in;
	final OutputStream out;
	final OutputStream err;

	public FeatureContext(String[] args, InputStream in, OutputStream out, OutputStream err) {
		super();
		this.args = args;
		this.in = in;
		this.out = out;
		this.err = err;
	}

	public String[] getArgs() {
		return args;
	}

	public InputStream getIn() {
		return in;
	}

	public OutputStream getOut() {
		return out;
	}

	public OutputStream getErr() {
		return err;
	}

}
