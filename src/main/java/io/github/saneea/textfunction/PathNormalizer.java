package io.github.saneea.textfunction;

public abstract class PathNormalizer implements TextFunction {

	protected abstract String getSlash();

	@Override
	public String apply(String s) {
		return s.replaceAll("(\\\\|\\/)+", "\\" + getSlash());
	}

}
