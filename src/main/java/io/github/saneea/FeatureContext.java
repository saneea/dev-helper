package io.github.saneea;

public class FeatureContext {
	final String[] args;
	final AppContext appContext;

	public FeatureContext(String[] args, AppContext appContext) {
		this.args = args;
		this.appContext = appContext;
	}

	public String[] getArgs() {
		return args;
	}

	public AppContext getAppContext() {
		return appContext;
	}

}
