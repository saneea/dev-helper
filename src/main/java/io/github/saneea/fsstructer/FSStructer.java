package io.github.saneea.fsstructer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FSStructer {

	private final Path pathToBadStructure;
	private final Path pathToGoodStructure;

	public FSStructer(Path pathToBadStructure, Path pathToGoodStructure) {
		this.pathToBadStructure = pathToBadStructure;
		this.pathToGoodStructure = pathToGoodStructure;
	}

	public void execute() throws IOException {
		Files.walk(pathToBadStructure);
		//pathToBadStructure.
	}
}
