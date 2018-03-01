package io.github.saneea.textfunction;

public class WindowsPathNormalizer extends PathNormalizer {

	@Override
	protected String getSlash() {
		return "\\";
	}

}
