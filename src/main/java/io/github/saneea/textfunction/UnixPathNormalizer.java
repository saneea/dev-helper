package io.github.saneea.textfunction;

public class UnixPathNormalizer extends PathNormalizer {

	@Override
	protected String getSlash() {
		return "/";
	}

}
