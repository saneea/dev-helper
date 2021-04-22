package io.github.saneea.dvh.feature;

import io.github.saneea.dvh.TestUtils;
import io.github.saneea.dvh.feature.xml.XmlPrettyPrint;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(value = Parameterized.class)
public class XmlReformTest {

	@Parameter
	public String testId;

	private static final Path TEST_CASES_ROOT = TestUtils.TESTS_RESOURCES.resolve("xml_pretty_print");

	@Parameters(name = "{0}")
	public static List<String[]> testIds() {
		return TestUtils.testIds(TEST_CASES_ROOT);
	}

	@Test
	public void test() throws Exception {
		Path testRootDir = TEST_CASES_ROOT.resolve(testId);

		String actual = prettyPrint(testRootDir.resolve("input.xml").toString());

		String expected = TestUtils.readFile(testRootDir.resolve("expected.xml"));

		assertEquals(expected, actual);
	}

	private String prettyPrint(String inputFilePath) throws Exception {
		try (Reader inputStream = new InputStreamReader(//
				new BufferedInputStream(//
						new FileInputStream(inputFilePath)), //
				StandardCharsets.UTF_8); //
				Writer outputWriter = new StringWriter()) {
			XmlPrettyPrint.Companion.run(inputStream, outputWriter, false);
			return outputWriter.toString();
		}
	}

}
