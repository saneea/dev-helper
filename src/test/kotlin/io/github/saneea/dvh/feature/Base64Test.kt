package io.github.saneea.dvh.feature

import io.github.saneea.dvh.FeatureContext
import io.github.saneea.dvh.TESTS_RESOURCES
import io.github.saneea.dvh.feature.binary.base64.FromBase64
import io.github.saneea.dvh.feature.binary.base64.ToBase64
import io.github.saneea.dvh.readFile
import org.junit.Assert
import org.junit.Test
import java.io.BufferedInputStream
import java.io.ByteArrayOutputStream
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path

private val TEST_CASES_ROOT = TESTS_RESOURCES.resolve("base64")
private val BASE64_FULL_SET_FILE = TEST_CASES_ROOT.resolve("full-bytes-set.base64")

class Base64Test {

    @Test
    fun testFromBase64FullSet() {
        val actual = fromBase64(BASE64_FULL_SET_FILE)
        val expected = Files.readAllBytes(BYTES_FULL_SET_FILE)
        Assert.assertArrayEquals(expected, actual)
    }

    @Test
    fun testToBase64FullSet() {
        val actual = toBase64(BYTES_FULL_SET_FILE)
        val expected = readFile(BASE64_FULL_SET_FILE)
        Assert.assertEquals(expected, actual)
    }

    private fun fromBase64(inputFilePath: Path): ByteArray {
        BufferedInputStream(Files.newInputStream(inputFilePath)).use { inputStream ->
            ByteArrayOutputStream().use { output ->
                val fromBase64 = FromBase64()
                fromBase64.inBinStream = inputStream
                fromBase64.outBinStream = output
                fromBase64.run()
                return output.toByteArray()
            }
        }
    }

    private fun toBase64(inputFilePath: Path): String {
        BufferedInputStream(Files.newInputStream(inputFilePath)).use { inputStream ->
            ByteArrayOutputStream().use { output ->
                val toBase64 = ToBase64()
                toBase64.inBinStream = inputStream
                toBase64.outBinStream = output
                toBase64.run()
                return output.toString(StandardCharsets.UTF_8)
            }
        }
    }
}