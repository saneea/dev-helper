package io.github.saneea.dvh.textfunction

import io.github.saneea.dvh.FeatureContext
import io.github.saneea.dvh.feature.text.ToCharArray
import org.junit.Assert
import org.junit.Test
import java.io.StringReader
import java.io.StringWriter

class String2CppArrayTest {

    @Test
    fun testSimple() = test("abc", "'a', 'b', 'c'")

    @Test
    fun testEscapingQuote() = test("a'bc", "'a', '\\'', 'b', 'c'")

    @Test
    fun testEscapingBackslash() = test("a\\bc", "'a', '\\\\', 'b', 'c'")

    private fun test(input: String, expected: String) {
        val toCharArray = ToCharArray()
        toCharArray.setInTextReader(StringReader(input))
        val out = StringWriter()
        toCharArray.outTextWriter = out

        val featureContext = FeatureContext(null, "any", emptyArray())
        toCharArray.run(featureContext)
        val actual = out.toString()
        Assert.assertEquals(expected, actual)
    }
}