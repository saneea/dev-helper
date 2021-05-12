package io.github.saneea.dvh.utils

import io.github.saneea.dvh.utils.ByteSequenceRecognizer.ByteNode
import org.junit.Assert
import org.junit.Test
import java.io.ByteArrayInputStream
import java.nio.charset.StandardCharsets

private val TEST_CHARSET = StandardCharsets.US_ASCII

class ByteSequenceRecognizerTest {

    @Test
    fun testTwoBranches() {
        val sequences = listOf("123", "124")
        test("123", 0, sequences)
        test("124", 1, sequences)
        test("12345678", 0, sequences)
        test("12456789", 1, sequences)
        test("12", null, sequences)
        test("12X", null, sequences)
        test("12X", null, sequences)
        test("", null, sequences)
    }

    @Test
    fun testDifferentSize() {
        val sequences = listOf("123", "1234")
        test("123", 0, sequences)
        test("1234", 0, sequences)
    }

    @Test
    fun testEmpty() {
        val sequences = listOf("", "123")
        test("123", 0, sequences)
        test("1234", 0, sequences)
        test("", 0, sequences)
    }

    @Test(expected = IllegalArgumentException::class)
    fun testNoSequences() {
        val sequences = emptyList<String>()
        test("123", null, sequences)
        test("1234", null, sequences)
        test("", null, sequences)
    }

    @Test
    fun testTwoBranchesLong() {
        val sequences = listOf("123abcdef", "124abcdef")
        test("123abcdef", 0, sequences)
        test("124abcdef", 1, sequences)
        test("123abcdefXYZ", 0, sequences)
        test("124abcdefXYZ", 1, sequences)
        test("123abcde", null, sequences)
        test("124abcde", null, sequences)
        test("123abcdeX", null, sequences)
        test("124abcdeX", null, sequences)
        test("", null, sequences)
    }

    @Test
    fun testDepth() {
        testDepthInternal()
        testDepthInternal("")
        testDepthInternal("", "1")
        testDepthInternal("1", "")
        testDepthInternal("1")
        testDepthInternal("1", "2")
        testDepthInternal("1", "22")
        testDepthInternal("11", "2")
        testDepthInternal("11", "22")
        testDepthInternal("11", "222")
        testDepthInternal("111", "222")
        testDepthInternal("111", "222", "1111111")
    }

    private fun testDepthInternal(vararg sequences: String) {
        val sequencesList = sequences.toList()

        val node = sequencesMap(sequencesList).byteNode()

        val expectedDepth = sequencesList
            .map(String::length)
            .maxOrNull() ?: 0

        Assert.assertEquals(expectedDepth, node.depth)
    }

    @Test
    fun testAllBytesValues() {
        val branchId = 1
        for (branchByte in allBytes()) {
            val sequence = intArrayOf(branchByte.toInt() and 0xff)

            val sequencesTree = mapOf(sequence to branchId).byteNode()
            for (inputByte in allBytes()) {
                val inputData = byteArrayOf(inputByte)
                ByteSequenceRecognizer(
                    ByteArrayInputStream(inputData),
                    sequencesTree
                ).use { bsr ->
                    val actualBytes = bsr.stream.readAllBytes()
                    Assert.assertArrayEquals(inputData, actualBytes)
                    val actualResult = bsr.result
                    val expectedResult = if (branchByte == inputByte)
                        branchId
                    else
                        null
                    Assert.assertEquals(expectedResult, actualResult)
                }
            }
        }
    }

    private fun test(inputStr: String, expectedResult: Int?, sequences: List<String>) {
        recognizer(
            inputStr,
            sequencesMap(sequences).byteNode()
        ).use { bsr ->
            val actualText = bsr.stream.readAllBytes().string
            Assert.assertEquals(inputStr, actualText)
            val actualResult = bsr.result
            Assert.assertEquals(expectedResult, actualResult)
        }
    }

    companion object {

        private fun allBytes(): Iterable<Byte> {
            return Iterable {
                object : Iterator<Byte> {
                    var next: Byte? = Byte.MIN_VALUE

                    override fun hasNext(): Boolean {
                        return next != null
                    }

                    override fun next(): Byte {
                        val ret = next
                        next = if (next == Byte.MAX_VALUE) {
                            null
                        } else {
                            (next!! + 1).toByte()
                        }
                        return ret!!
                    }
                }
            }
        }
    }
}

private val String.intArray
    get() = this.bytes.map(Byte::toInt).toIntArray()

private fun recognizer(s: String, sequencesTree: ByteNode<Int>) =
    ByteSequenceRecognizer(s.bytes.inputStream(), sequencesTree)

private val String.bytes
    get() = this.toByteArray(TEST_CHARSET)

private val ByteArray.string
    get() = String(this, TEST_CHARSET)

private fun sequencesMap(sequences: List<String>): Map<IntArray, Int> {
    val r: MutableMap<IntArray, Int> = HashMap()
    for (i in sequences.indices) {
        r[sequences[i].intArray] = i
    }
    return r
}