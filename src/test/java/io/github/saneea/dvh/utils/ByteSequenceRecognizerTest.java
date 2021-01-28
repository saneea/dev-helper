package io.github.saneea.dvh.utils;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import org.junit.Assert;
import org.junit.Test;

import io.github.saneea.dvh.utils.ByteSequenceRecognizer.ByteNode;

public class ByteSequenceRecognizerTest {

	private static final Charset TEST_CHARSET = StandardCharsets.US_ASCII;

	@Test
	public void testTwoBranches() throws IOException {
		String[] sequences = { "123", "124" };
		test("123", Optional.of(0), sequences);
		test("124", Optional.of(1), sequences);

		test("12345678", Optional.of(0), sequences);
		test("12456789", Optional.of(1), sequences);

		test("12", Optional.empty(), sequences);

		test("12X", Optional.empty(), sequences);
		test("12X", Optional.empty(), sequences);

		test("", Optional.empty(), sequences);
	}

	@Test
	public void testDifferentSize() throws IOException {
		String[] sequences = { "123", "1234" };
		test("123", Optional.of(0), sequences);
		test("1234", Optional.of(0), sequences);
	}

	@Test
	public void testEmpty() throws IOException {
		String[] sequences = { "", "123" };
		test("123", Optional.of(0), sequences);
		test("1234", Optional.of(0), sequences);
		test("", Optional.of(0), sequences);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNoSequences() throws IOException {
		String[] sequences = {};
		test("123", Optional.empty(), sequences);
		test("1234", Optional.empty(), sequences);
		test("", Optional.empty(), sequences);
	}

	@Test
	public void testTwoBranchesLong() throws IOException {
		String[] sequences = { "123abcdef", "124abcdef" };
		test("123abcdef", Optional.of(0), sequences);
		test("124abcdef", Optional.of(1), sequences);

		test("123abcdefXYZ", Optional.of(0), sequences);
		test("124abcdefXYZ", Optional.of(1), sequences);

		test("123abcde", Optional.empty(), sequences);
		test("124abcde", Optional.empty(), sequences);

		test("123abcdeX", Optional.empty(), sequences);
		test("124abcdeX", Optional.empty(), sequences);

		test("", Optional.empty(), sequences);
	}

	@Test
	public void testDepth() throws IOException {
		testDepthInternal();
		testDepthInternal("");
		testDepthInternal("", "1");
		testDepthInternal("1", "");
		testDepthInternal("1");
		testDepthInternal("1", "2");
		testDepthInternal("1", "22");
		testDepthInternal("11", "2");
		testDepthInternal("11", "22");
		testDepthInternal("11", "222");
		testDepthInternal("111", "222");
		testDepthInternal("111", "222", "1111111");
	}

	private void testDepthInternal(String... sequences) throws IOException {
		ByteNode<Integer> node = ByteSequenceRecognizer.ByteNode.fromMap(//
				sequencesMap(//
						sequences));

		int expectedDepth = Stream//
				.of(sequences)//
				.mapToInt(String::length)//
				.max().orElse(0);

		assertEquals(expectedDepth, node.getDepth());
	}

	@Test
	public void testAllBytesValues() throws IOException {

		int branchId = 1;

		for (byte branchByte : allBytes()) {

			int[] sequence = { branchByte & 0xff };
			ByteNode<Integer> sequencesTree = ByteSequenceRecognizer.ByteNode.fromMap(Map.of(sequence, branchId));

			for (byte inputByte : allBytes()) {

				byte[] inputData = { inputByte };
				try (ByteSequenceRecognizer<Integer> bsr = new ByteSequenceRecognizer<>(//
						new ByteArrayInputStream(inputData), //
						sequencesTree)) {

					byte[] actualBytes = bsr.stream().readAllBytes();
					Assert.assertArrayEquals(inputData, actualBytes);

					Optional<Integer> actualResult = bsr.result();
					Optional<Integer> expectedResult = branchByte == inputByte//
							? Optional.of(branchId)//
							: Optional.empty();
					assertEquals(expectedResult, actualResult);
				}
			}
		}

		// Assert.fail("not implemented");
	}

	private static Iterable<Byte> allBytes() {
		return () -> new Iterator<Byte>() {

			Byte next = Byte.MIN_VALUE;

			@Override
			public boolean hasNext() {
				return next != null;
			}

			@Override
			public Byte next() {
				byte ret = next;
				if (next == Byte.MAX_VALUE) {
					next = null;
				} else {
					next++;
				}
				return ret;
			}

		};
	}

	private void test(String inputStr, Optional<Integer> expectedResult, String... sequences) throws IOException {
		try (ByteSequenceRecognizer<Integer> bsr = recognizer(//
				inputStr, //
				ByteSequenceRecognizer.ByteNode.fromMap(//
						sequencesMap(//
								sequences)))) {

			String actualText = asString(bsr.stream().readAllBytes());
			assertEquals(inputStr, actualText);

			Optional<Integer> actualResult = bsr.result();
			assertEquals(expectedResult, actualResult);
		}
	}

	private static String asString(byte[] bytes) {
		return new String(bytes, TEST_CHARSET);
	}

	private static Map<int[], Integer> sequencesMap(String... sequences) {
		Map<int[], Integer> r = new HashMap<>();
		for (int i = 0; i < sequences.length; ++i) {
			r.put(intArray(sequences[i]), i);
		}
		return r;
	}

	private static int[] intArray(String s) {
		return intArray(bytes(s));
	}

	private static int[] intArray(byte[] byteArray) {
		int r[] = new int[byteArray.length];
		for (int i = 0; i < byteArray.length; ++i) {
			r[i] = byteArray[i];
		}
		return r;
	}

	private static ByteSequenceRecognizer<Integer> recognizer(//
			String s, ByteNode<Integer> sequencesTree) throws IOException {
		return new ByteSequenceRecognizer<>(stream(s), sequencesTree);
	}

	private static InputStream stream(String s) {
		return new ByteArrayInputStream(bytes(s));
	}

	private static byte[] bytes(String s) {
		return s.getBytes(TEST_CHARSET);
	}
}
