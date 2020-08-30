package io.github.saneea.feature;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.Test;

import io.github.saneea.TestUtils;
import io.github.saneea.textfunction.FromBase64;
import io.github.saneea.textfunction.ToBase64;

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
			new FromBase64().run(inputStream, output, new String[] {});
			return output.toByteArray();
		}
	}

	private String toBase64(Path inputFilePath) throws Exception {
		try (InputStream inputStream = new BufferedInputStream(Files.newInputStream(inputFilePath)); //
				ByteArrayOutputStream output = new ByteArrayOutputStream()) {
			new ToBase64().run(inputStream, output, new String[] {});
			return new String(output.toByteArray(), StandardCharsets.UTF_8);
		}
	}

}
