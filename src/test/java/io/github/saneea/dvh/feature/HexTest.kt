package io.github.saneea.dvh.feature

import io.github.saneea.dvh.FeatureContext
import io.github.saneea.dvh.TESTS_RESOURCES
import io.github.saneea.dvh.feature.binary.hex.FromHex
import io.github.saneea.dvh.feature.binary.hex.ToHex
import io.github.saneea.dvh.readFile
import org.junit.Assert
import org.junit.Test
import java.io.BufferedInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStreamReader
import java.io.PrintStream
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path

private val TEST_CHARSET = StandardCharsets.UTF_8
private val TEST_CASES_ROOT = TESTS_RESOURCES.resolve("hex")
private val HEX_FULL_SET_FILE = TEST_CASES_ROOT.resolve("full-bytes-set.hex")
val BYTES_FULL_SET_FILE: Path = TESTS_RESOURCES.resolve("full-bytes-set.dat")

class HexTest {

    @Test
    fun testFromHexFullSet() {
        val actual = fromHex(HEX_FULL_SET_FILE)
        val expected = Files.readAllBytes(BYTES_FULL_SET_FILE)
        Assert.assertArrayEquals(expected, actual)
    }

    @Test
    fun testToHexFullSet() {
        val actual = toHex(BYTES_FULL_SET_FILE)
        val expected = readFile(HEX_FULL_SET_FILE)
        Assert.assertEquals(expected, actual)
    }

    private fun fromHex(inputFilePath: Path): ByteArray {
        InputStreamReader(
            BufferedInputStream(Files.newInputStream(inputFilePath))
        ).use { reader ->
            ByteArrayOutputStream().use { output ->
                val feature = FromHex()
                feature.setIn(reader)
                feature.setOutBinStream(output)
                feature.run(FeatureContext(null, "", arrayOf()))
                return output.toByteArray()
            }
        }
    }

    private fun toHex(inputFilePath: Path): String {
        BufferedInputStream(Files.newInputStream(inputFilePath)).use { inputStream ->
            ByteArrayOutputStream().use { output ->
                val feature = ToHex()
                PrintStream(output, false, TEST_CHARSET).use { printStreamOut ->
                    feature.setInBinStream(inputStream)
                    feature.setOutTextPrintStream(printStreamOut)
                    feature.run(FeatureContext(null, "", arrayOf()))
                }
                return output.toString(TEST_CHARSET)
            }
        }
    }
}