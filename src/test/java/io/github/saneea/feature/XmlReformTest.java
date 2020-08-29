package io.github.saneea.feature;

import static org.junit.Assert.assertEquals;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.file.Path;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import io.github.saneea.TestUtils;

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
		try (InputStream inputStream = new BufferedInputStream(new FileInputStream(inputFilePath)); //
				Writer outputWriter = new StringWriter()) {
			XmlReform.execute(inputStream, outputWriter);
			return outputWriter.toString();
		}
	}

}
