package io.github.saneea.textfunction;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class String2CppArrayTest {

	@Test
	public void testSimple() {
		test("abc", "'a','b','c'");
	}

	@Test
	public void testEscapingQuote() {
		test("a'bc", "'a','\\'','b','c'");
	}

	@Test
	public void testEscapingBackslash() {
		test("a\\bc", "'a','\\\\','b','c'");
	}

	private void test(String input, String expected) {
		String actual = new String2CharArray().apply(input);
		assertEquals(expected, actual);
	}

}
