package io.github.saneea.fsstructer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

public class FSStructer {

	private static final String SKIPPED_FOLDER = "_skipped_";

	private final Path pathToBadStructure;
	private final Path pathToGoodStructure;
	private final Scanner input = new Scanner(System.in);

	private static class MovementProposition {

		public enum Type {
			SINGLE, MULTI, NONE
		}

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

		public Type getType() {
			switch (proposition.size()) {
			case 0:
				return Type.NONE;
			case 1:
				return Type.SINGLE;
			default:
				return Type.MULTI;
			}
		}
	}

	private static class MovementDecision {
		private final Path orig;

		private final Path decision;

		public MovementDecision(Path orig, Path decision) {
			this.orig = orig;
			this.decision = decision;
		}

		public Path getOrig() {
			return orig;
		}

		public Path getDecision() {
			return decision;
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
		List<MovementDecision> decisions = propositionsToDecisions(propositions);
	}

	private List<MovementDecision> propositionsToDecisions(List<MovementProposition> propositions) {
		List<MovementDecision> decisions = new ArrayList<>();

		Map<MovementProposition.Type, List<MovementProposition>> sortedPropositions = sortPropositions(propositions);

		for (MovementProposition proposition : sortedPropositions.get(MovementProposition.Type.SINGLE)) {
			MovementDecision decision = new MovementDecision(proposition.getOrig(),
					proposition.getProposition().get(0));
			decisions.add(decision);
		}

		for (MovementProposition proposition : sortedPropositions.get(MovementProposition.Type.NONE)) {
			MovementDecision decision = new MovementDecision(proposition.getOrig(), null);
			decisions.add(decision);
		}

		for (MovementProposition proposition : sortedPropositions.get(MovementProposition.Type.MULTI)) {
			MovementDecision decision = askUserForDecision(proposition);
			decisions.add(decision);
		}

		return decisions;
	}

	private MovementDecision askUserForDecision(MovementProposition proposition) {
		List<Path> targetPaths = proposition.getProposition();
		int targetPathsCount = targetPaths.size();
		Path orig = proposition.getOrig();

		while (true) {
			System.out.println(//
					"File \"" + orig + "\" (" + orig.toFile().length() + " bytes)" + //
							" was found in " + targetPathsCount + " locations:");
			for (int i = 0; i < targetPathsCount; ++i) {
				Path targetPath = targetPaths.get(i);
				System.out.println(i + ". " + targetPath + "(" + targetPath.toFile().length() + " bytes)");
			}

			System.out.println("Please enter command:");
			System.out.println("r(reject): file will be skipped (will moved to folder \"" + SKIPPED_FOLDER + "\")");
			System.out.println("s(select): select target index");

			String command = input.nextLine();
			switch (command.toLowerCase().trim()) {
			case "s":
				return new MovementDecision(orig, null);
			case "c": {
				do {
					System.out.println(//
							"Enter target index from " + 0 + " to " + (targetPathsCount - 1) + " (inclusive)");
					String targetIndexStr = input.nextLine();
					try {
						int targetIndex = Integer.valueOf(targetIndexStr);
						if (0 <= targetIndex && targetIndex < targetPathsCount) {
							return new MovementDecision(orig, targetPaths.get(targetIndex));
						} else {
							System.out.println("Index " + targetIndex + " is out of range");
						}
					} catch (Exception e) {
						System.out.println(e.getMessage());
					}
				} while (true);
			}
			}
		}
	}

	private EnumMap<MovementProposition.Type, List<MovementProposition>> sortPropositions(
			List<MovementProposition> propositions) {
		EnumMap<MovementProposition.Type, List<MovementProposition>> ret = new EnumMap<>(
				MovementProposition.Type.class);

		for (MovementProposition.Type type : MovementProposition.Type.values()) {
			ret.put(type, new ArrayList<>());
		}

		for (MovementProposition proposition : propositions) {
			List<MovementProposition> propositionForType = ret.get(proposition.getType());
			propositionForType.add(proposition);
		}
		return ret;
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
