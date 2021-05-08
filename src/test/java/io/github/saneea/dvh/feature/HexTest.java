package io.github.saneea.dvh.feature;

import io.github.saneea.dvh.FeatureContext;
import io.github.saneea.dvh.TestUtils;
import io.github.saneea.dvh.feature.binary.hex.FromHex;
import io.github.saneea.dvh.feature.binary.hex.ToHex;
import org.junit.Test;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class HexTest {

	private static final Charset TEST_CHARSET = StandardCharsets.UTF_8;

	private static final Path TEST_CASES_ROOT = TestUtils.TESTS_RESOURCES.resolve("hex");

	private static final Path HEX_FULL_SET_FILE = TEST_CASES_ROOT.resolve("full-bytes-set.hex");
	public static final Path BYTES_FULL_SET_FILE = TestUtils.TESTS_RESOURCES.resolve("full-bytes-set.dat");

	@Test
	public void testFromHexFullSet() throws Exception {

		byte[] actual = fromHex(HEX_FULL_SET_FILE);
		byte[] expected = Files.readAllBytes(BYTES_FULL_SET_FILE);

		assertArrayEquals(expected, actual);
	}

	@Test
	public void testToHexFullSet() throws Exception {

		String actual = toHex(BYTES_FULL_SET_FILE);
		String expected = TestUtils.readFile(HEX_FULL_SET_FILE);

		assertEquals(expected, actual);
	}

	private byte[] fromHex(Path inputFilePath) throws Exception {
		try (Reader reader = new InputStreamReader(//
				new BufferedInputStream(Files.newInputStream(inputFilePath))); //
				ByteArrayOutputStream output = new ByteArrayOutputStream()) {
			FromHex feature = new FromHex();
			feature.setIn(reader);
			feature.setOut(output);
			feature.run(new FeatureContext(null, "", new String[]{}));
			return output.toByteArray();
		}
	}

	private String toHex(Path inputFilePath) throws Exception {
		try (InputStream inputStream = new BufferedInputStream(Files.newInputStream(inputFilePath)); //
				ByteArrayOutputStream output = new ByteArrayOutputStream()) {

			ToHex feature = new ToHex();

			try (PrintStream printStreamOut = new PrintStream(output, false, TEST_CHARSET)) {
				feature.setIn(inputStream);
				feature.setOut(printStreamOut);
				feature.run(new FeatureContext(null, "", new String[]{}));
			}

			return output.toString(TEST_CHARSET);
		}
	}

}
