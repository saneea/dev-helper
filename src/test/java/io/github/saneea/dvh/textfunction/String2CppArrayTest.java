package io.github.saneea.dvh.textfunction;

import io.github.saneea.dvh.FeatureContext;
import io.github.saneea.dvh.feature.text.ToCharArray;
import org.junit.Test;

import java.io.StringReader;
import java.io.StringWriter;

import static org.junit.Assert.assertEquals;

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

        FeatureContext featureContext = new FeatureContext(null, "any", new String[0]);
        toCharArray.run(featureContext);

        String actual = out.toString();
        assertEquals(expected, actual);
    }

}
