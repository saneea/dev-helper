package io.github.saneea;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;

public class FeatureContext {
	final String[] args;
	final InputStream in;
	final OutputStream out;
	final PrintStream err;
	final AppContext appContext;
	final String featureAlias;

	public FeatureContext(String[] args, InputStream in, OutputStream out, PrintStream err, AppContext appContext,
			String featureAlias) {
		this.args = args;
		this.in = in;
		this.out = out;
		this.err = err;
		this.appContext = appContext;
		this.featureAlias = featureAlias;
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

	public PrintStream getErr() {
		return err;
	}

	public AppContext getAppContext() {
		return appContext;
	}

	public String getFeatureAlias() {
		return featureAlias;
	}

}
