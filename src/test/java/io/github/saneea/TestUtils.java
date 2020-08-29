package io.github.saneea;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TestUtils {

	public static final Path TESTS_RESOURCES = Paths.get("src", "test", "resources", "tests");

	public static List<String[]> testIds(Path testCasesDir) {
		return Stream.of(//
				testCasesDir.toFile().list())//
				.map(dir -> new String[] { dir })//
				.collect(Collectors.toList());
	}

	public static String readFile(Path filePath) throws IOException {
		byte[] encoded = Files.readAllBytes(filePath);
		return new String(encoded, StandardCharsets.UTF_8);
	}
}
