package io.github.saneea.textfunction;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class String2CppArrayTest {

	@Test
	public void testSimple() {
		test("abc", "'a','b','c','\\0'");
	}

	@Test
	public void testEscapingQuote() {
		test("a'bc", "'a','\\'','b','c','\\0'");
	}

	@Test
	public void testEscapingBackslash() {
		test("a\\bc", "'a','\\\\','b','c','\\0'");
	}

	private void test(String input, String expected) {
		String actual = new String2CppArray().apply(input);
		assertEquals(expected, actual);
	}

}
