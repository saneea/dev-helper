package io.github.saneea.fsstructer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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

	public static void main(String[] args) throws IOException {
		new FSStructer(Paths.get(args[0]), Paths.get(args[1])).execute();
	}

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

	public FSStructer(Path pathToBadStructure, Path pathToGoodStructure) {
		this.pathToBadStructure = pathToBadStructure;
		this.pathToGoodStructure = pathToGoodStructure;
	}

	private List<Path> getInputPaths() throws IOException {
		return Files.walk(pathToBadStructure)//
				.filter(path -> path.toFile().isFile())//
				.collect(Collectors.toList());
	}

	public void execute() throws IOException {
		List<Path> inputPaths = getInputPaths();
		List<MovementProposition> propositions = getMovementPropositions(inputPaths);
		List<MovementDecision> decisions = propositionsToDecisions(propositions);

		for (MovementDecision decision : decisions) {
			Path fileToMove = decision.getOrig();
			Path fileInGoodStructure = decision.getDecision();
			Path relativeTargetPath = (fileInGoodStructure == null)//
					? Paths.get(SKIPPED_FOLDER).resolve(fileToMove.getFileName())//
					: pathToGoodStructure.relativize(fileInGoodStructure);
			Path absoluteTargetPath = pathToBadStructure.resolve(relativeTargetPath);
			absoluteTargetPath.getParent().toFile().mkdirs();
			Files.move(fileToMove, absoluteTargetPath);
		}
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

		long origFileSize = orig.toFile().length();

		while (true) {
			System.out.println(//
					"File [" + origFileSize + " bytes] \"" + orig + "\"" + //
							" was found in " + targetPathsCount + " locations:");

			int matchSizeCount = 0;

			for (int i = 0; i < targetPathsCount; ++i) {
				Path targetPath = targetPaths.get(i);
				long targetFileSize = targetPath.toFile().length();
				boolean matchSize = targetFileSize == origFileSize;
				if (matchSize) {
					++matchSizeCount;
				}
				System.out.println(//
						i + ". [" + (matchSize ? " * " : "")//
								+ targetFileSize + " bytes] " + targetPath);
			}

			if (matchSizeCount != 0) {
				System.out.println("Note: file" + (matchSizeCount > 1 ? "s" : "") + " marked with star ha"
						+ (matchSizeCount == 1 ? "s" : "ve") + " the same size");
			}
			System.out.println("Please enter command:");
			System.out.println("r(reject): file will be skipped (will moved to folder \"" + SKIPPED_FOLDER + "\")");
			System.out.println("s(select): select target index");

			String command = input.nextLine();
			switch (command.toLowerCase().trim()) {
			case "r":
				return new MovementDecision(orig, null);
			case "s": {
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
						System.out.println(e);
					}
				} while (true);
			}
			default:
				System.out.println("\"" + command + "\" is invalid command");
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
					findFilesByName(inputPath.getFileName())));
		}
		return propositions;
	}

	private List<Path> findFilesByName(Path fileName) throws IOException {
		return Files.find(pathToGoodStructure, Integer.MAX_VALUE, //
				(path, attributes) -> path.getFileName().equals(fileName))//
				.collect(Collectors.toList());
	}

}
