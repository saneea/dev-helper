package io.github.saneea.dvh.feature;

import io.github.saneea.dvh.FeatureContext;
import io.github.saneea.dvh.TestUtils;
import io.github.saneea.dvh.feature.binary.base64.FromBase64;
import io.github.saneea.dvh.feature.binary.base64.ToBase64;
import org.junit.Test;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class Base64Test {

	private static final Path TEST_CASES_ROOT = TestUtils.TESTS_RESOURCES.resolve("base64");

	private static final Path BASE64_FULL_SET_FILE = TEST_CASES_ROOT.resolve("full-bytes-set.base64");

	@Test
	public void testFromBase64FullSet() throws Exception {

		byte[] actual = fromBase64(BASE64_FULL_SET_FILE);
		byte[] expected = Files.readAllBytes(HexTest.BYTES_FULL_SET_FILE);

		assertArrayEquals(expected, actual);
	}

	@Test
	public void testToBase64FullSet() throws Exception {

		String actual = toBase64(HexTest.BYTES_FULL_SET_FILE);
		String expected = TestUtils.readFile(BASE64_FULL_SET_FILE);

		assertEquals(expected, actual);
	}

	private byte[] fromBase64(Path inputFilePath) throws Exception {
		try (InputStream inputStream = new BufferedInputStream(Files.newInputStream(inputFilePath)); //
				ByteArrayOutputStream output = new ByteArrayOutputStream()) {
			FromBase64 fromBase64 = new FromBase64();
			fromBase64.setIn(inputStream);
			fromBase64.setOut(output);
			fromBase64.run(new FeatureContext(null, "", new String[]{}));
			return output.toByteArray();
		}
	}

	private String toBase64(Path inputFilePath) throws Exception {
		try (InputStream inputStream = new BufferedInputStream(Files.newInputStream(inputFilePath)); //
				ByteArrayOutputStream output = new ByteArrayOutputStream()) {
			ToBase64 toBase64 = new ToBase64();
			toBase64.setIn(inputStream);
			toBase64.setOut(output);
			toBase64.run(new FeatureContext(null, "", new String[]{}));
			return output.toString(StandardCharsets.UTF_8);
		}
	}

}
