package io.github.saneea.fsstructer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FSStructer {

	private final Path pathToBadStructure;
	private final Path pathToGoodStructure;

	private static class MovementProposition {
		private final Path orig;

		private final List<Path> proposition;

		public MovementProposition(Path orig, List<Path> proposition) {
			this.orig = orig;
			this.proposition = proposition;
		}

		public Path getOrig() {
			return orig;
		}

		public List<Path> getProposition() {
			return proposition;
		}

	}

	private static class Movement {
	}

	public FSStructer(Path pathToBadStructure, Path pathToGoodStructure) {
		this.pathToBadStructure = pathToBadStructure;
		this.pathToGoodStructure = pathToGoodStructure;
	}

	private List<Path> getInputPaths() throws IOException {
		return Files.walk(pathToBadStructure).collect(Collectors.toList());
	}

	public void execute() throws IOException {
		List<Path> inputPaths = getInputPaths();

		List<MovementProposition> propositions = getMovementPropositions(inputPaths);

		for (MovementProposition proposition : propositions) {
			processPath(proposition);
		}
	}

	private List<MovementProposition> getMovementPropositions(List<Path> inputPaths) throws IOException {
		List<MovementProposition> propositions = new ArrayList<>();
		for (Path inputPath : inputPaths) {
			propositions.add(new MovementProposition(//
					inputPath, //
					findFilesByName(inputPath.getFileName().toString())));
		}
		return propositions;
	}

	private List<Path> findFilesByName(String fileName) throws IOException {
		return Files.find(pathToGoodStructure, Integer.MAX_VALUE, //
				(path, attributes) -> path.getFileName().toString().equals(fileName))//
				.collect(Collectors.toList());
	}

	private void processPath(MovementProposition proposition) {
		// TODO Auto-generated method stub

	}

	public void execute2() throws IOException {
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

		public void move() {
			try {
				Files.move(origPath, newPath);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}
}
