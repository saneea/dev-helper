package io.github.saneea.dvh.textfunction;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UncheckedIOException;

import org.junit.Test;

import io.github.saneea.dvh.feature.text.ToCharArray;

public class String2CppArrayTest {

	@Test
	public void testSimple() {
		test("abc", "'a', 'b', 'c'");
	}

	@Test
	public void testEscapingQuote() {
		test("a'bc", "'a', '\\'', 'b', 'c'");
	}

	@Test
	public void testEscapingBackslash() {
		test("a\\bc", "'a', '\\\\', 'b', 'c'");
	}

	private void test(String input, String expected) {
		ToCharArray toCharArray = new ToCharArray();
		toCharArray.setIn(new StringReader(input));
		StringWriter out = new StringWriter();
		toCharArray.setOut(out);

		try {
			toCharArray.run(null);
		} catch (IOException e) {
			new UncheckedIOException(e);
		}

		String actual = out.toString();
		assertEquals(expected, actual);
	}

}
