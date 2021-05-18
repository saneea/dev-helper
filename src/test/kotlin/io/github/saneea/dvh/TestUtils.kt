package io.github.saneea.dvh

import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.stream.Collectors
import java.util.stream.Stream

val TESTS_RESOURCES: Path = Paths.get("src", "test", "resources", "tests")

fun testIds(testCasesDir: Path): List<Array<String>> {
    return Stream.of(
        *testCasesDir.toFile().list()
    )
        .map { dir: String -> arrayOf(dir) }
        .collect(Collectors.toList())
}

fun readFile(filePath: Path) =
    String(Files.readAllBytes(filePath), StandardCharsets.UTF_8)
