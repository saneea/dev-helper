package io.github.saneea.fsstructer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FSStructer {

	private final Path pathToBadStructure;
	private final Path pathToGoodStructure;

	public FSStructer(Path pathToBadStructure, Path pathToGoodStructure) {
		this.pathToBadStructure = pathToBadStructure;
		this.pathToGoodStructure = pathToGoodStructure;
	}

	public void execute() throws IOException {
		Files.walk(pathToBadStructure)//
				.map(filePath -> {

					String fileName = filePath.getFileName().toString();

					List<Path> filesInGood;

					try {
						filesInGood = Files.find(pathToGoodStructure, Integer.MAX_VALUE, //
								(p, atr) -> p.getFileName().toString().equals(fileName))//
								.collect(Collectors.toList());
					} catch (IOException e) {
						throw new RuntimeException(e);
					}

					if (filesInGood.isEmpty()) {
						return null;
					}

					if (filesInGood.size() == 1) {
						return new FileMovement(filePath, filesInGood.get(0));
					}

					return null;

				}).forEach(FileMovement::move);

		// pathToBadStructure.
	}

	static class FileMovement {
		private final Path origPath;
		private final Path newPath;

		public FileMovement(Path origPath, Path newPath) {
			this.origPath = origPath;
			this.newPath = newPath;
		}

		public void move() throws IOException {
			Files.move(origPath, newPath);
		}

	}
}
