package io.github.saneea;

import java.io.InputStream;
import java.io.OutputStream;

public class FeatureContext {
	final String[] args;
	final InputStream in;
	final OutputStream out;
	final OutputStream err;
	final AppContext appContext;

	public FeatureContext(String[] args, InputStream in, OutputStream out, OutputStream err, AppContext appContext) {
		this.args = args;
		this.in = in;
		this.out = out;
		this.err = err;
		this.appContext = appContext;
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

	public AppContext getAppContext() {
		return appContext;
	}

}
