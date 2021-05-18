package io.github.saneea.dvh.feature

import io.github.saneea.dvh.TESTS_RESOURCES
import io.github.saneea.dvh.feature.xml.XmlPrettyPrint.Companion.run
import io.github.saneea.dvh.readFile
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.io.BufferedInputStream
import java.io.FileInputStream
import java.io.InputStreamReader
import java.io.StringWriter
import java.nio.charset.StandardCharsets

@RunWith(value = Parameterized::class)
class XmlReformTest {

    @Parameterized.Parameter
    lateinit var testId: String

    @Test
    fun test() {
        val testRootDir = TEST_CASES_ROOT.resolve(testId)
        val actual = prettyPrint(testRootDir.resolve("input.xml").toString())
        val expected = readFile(testRootDir.resolve("expected.xml"))
        Assert.assertEquals(expected, actual)
    }

    private fun prettyPrint(inputFilePath: String): String {
        InputStreamReader(
            BufferedInputStream(
                FileInputStream(inputFilePath)
            ),
            StandardCharsets.UTF_8
        ).use { inputStream ->
            StringWriter().use { outputWriter ->
                run(inputStream, outputWriter, false)
                return outputWriter.toString()
            }
        }
    }

    companion object {
        private val TEST_CASES_ROOT = TESTS_RESOURCES.resolve("xml_pretty_print")

        @Parameterized.Parameters(name = "{0}")
        @JvmStatic
        fun testIds(): List<Array<String>> {
            return io.github.saneea.dvh.testIds(TEST_CASES_ROOT)
        }
    }
}