package io.github.saneea.textfunction;

import static org.junit.Assert.assertEquals;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

@RunWith(value = Parameterized.class)
public class XmlPrettyPrintTest {

	@Parameter
	public String testId;

	private static final Path TEST_CASES_ROOT = Paths.get("src", "test", "resources", "tests", "xml_pretty_print");

	@Parameters(name = "{0}")
	public static List<String[]> testIds() {
		return Stream.of(//
				TEST_CASES_ROOT.toFile().list())//
				.map(dir -> new String[] { dir })//
				.collect(Collectors.toList());
	}

	@Test
	public void test() throws Exception {
		Path testRootDir = TEST_CASES_ROOT.resolve(testId);

		String actual = prettyPrint(testRootDir.resolve("input.xml").toString());

		String expected = readFile(testRootDir.resolve("expected.xml"));

		assertEquals(expected, actual);
	}

	private String readFile(Path filePath) throws IOException {
		byte[] encoded = Files.readAllBytes(filePath);
		return new String(encoded, StandardCharsets.UTF_8);
	}

	private String prettyPrint(String inputFilePath) throws Exception {
		try (InputStream inputStream = new BufferedInputStream(new FileInputStream(inputFilePath)); //
				Writer outputWriter = new StringWriter()) {
			XmlPrettyPrint.execute(inputStream, outputWriter);
			return outputWriter.toString();
		}
	}

}
