package io.github.saneea.dvh.utils

import io.github.saneea.dvh.utils.ByteSequenceRecognizer.ByteNode
import io.github.saneea.dvh.utils.ByteSequenceRecognizer.ByteNode.Companion.fromMap
import org.junit.Assert
import org.junit.Test
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.nio.charset.StandardCharsets
import java.util.*
import java.util.stream.Stream

class ByteSequenceRecognizerTest {

    @Test
    fun testTwoBranches() {
        val sequences = arrayOf("123", "124")
        test("123", Optional.of(0), *sequences)
        test("124", Optional.of(1), *sequences)
        test("12345678", Optional.of(0), *sequences)
        test("12456789", Optional.of(1), *sequences)
        test("12", Optional.empty(), *sequences)
        test("12X", Optional.empty(), *sequences)
        test("12X", Optional.empty(), *sequences)
        test("", Optional.empty(), *sequences)
    }

    @Test
    fun testDifferentSize() {
        val sequences = arrayOf("123", "1234")
        test("123", Optional.of(0), *sequences)
        test("1234", Optional.of(0), *sequences)
    }

    @Test
    fun testEmpty() {
        val sequences = arrayOf("", "123")
        test("123", Optional.of(0), *sequences)
        test("1234", Optional.of(0), *sequences)
        test("", Optional.of(0), *sequences)
    }

    @Test(expected = IllegalArgumentException::class)
    fun testNoSequences() {
        val sequences = arrayOf<String>()
        test("123", Optional.empty(), *sequences)
        test("1234", Optional.empty(), *sequences)
        test("", Optional.empty(), *sequences)
    }

    @Test
    fun testTwoBranchesLong() {
        val sequences = arrayOf("123abcdef", "124abcdef")
        test("123abcdef", Optional.of(0), *sequences)
        test("124abcdef", Optional.of(1), *sequences)
        test("123abcdefXYZ", Optional.of(0), *sequences)
        test("124abcdefXYZ", Optional.of(1), *sequences)
        test("123abcde", Optional.empty(), *sequences)
        test("124abcde", Optional.empty(), *sequences)
        test("123abcdeX", Optional.empty(), *sequences)
        test("124abcdeX", Optional.empty(), *sequences)
        test("", Optional.empty(), *sequences)
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
        val node = fromMap( //
            sequencesMap( //
                *sequences
            )
        )
        val expectedDepth = Stream //
            .of(*sequences) //
            .mapToInt { obj: String -> obj.length } //
            .max().orElse(0)
        Assert.assertEquals(expectedDepth.toLong(), node.depth.toLong())
    }

    @Test
    fun testAllBytesValues() {
        val branchId = 1
        for (branchByte in allBytes()) {
            val sequence = intArrayOf(branchByte.toInt() and 0xff)
            val sequencesTree = fromMap(java.util.Map.of(sequence, branchId))
            for (inputByte in allBytes()) {
                val inputData = byteArrayOf(inputByte)
                ByteSequenceRecognizer( //
                    ByteArrayInputStream(inputData),  //
                    sequencesTree
                ).use { bsr ->
                    val actualBytes = bsr.stream().readAllBytes()
                    Assert.assertArrayEquals(inputData, actualBytes)
                    val actualResult = bsr.result()
                    val expectedResult = if (branchByte == inputByte //
                    ) Optional.of(branchId) //
                    else Optional.empty()
                    Assert.assertEquals(expectedResult, actualResult)
                }
            }
        }
    }

    private fun test(inputStr: String, expectedResult: Optional<Int>, vararg sequences: String) {
        recognizer( //
            inputStr,  //
            fromMap( //
                sequencesMap( //
                    *sequences
                )
            )
        ).use { bsr ->
            val actualText = asString(bsr.stream().readAllBytes())
            Assert.assertEquals(inputStr, actualText)
            val actualResult = bsr.result()
            Assert.assertEquals(expectedResult, actualResult)
        }
    }

    companion object {
        private val TEST_CHARSET = StandardCharsets.US_ASCII
        private fun allBytes(): Iterable<Byte> {
            return Iterable {
                object : Iterator<Byte> {
                    var next: Byte? = Byte.MIN_VALUE
                    override fun hasNext(): Boolean {
                        return next != null
                    }

                    override fun next(): Byte {
                        val ret = next
                        if (next == Byte.MAX_VALUE) {
                            next = null
                        } else {
                            next = (next!! + 1).toByte()
                        }
                        return ret!!
                    }
                }
            }
        }

        private fun asString(bytes: ByteArray): String {
            return String(bytes, TEST_CHARSET)
        }

        private fun sequencesMap(vararg sequences: String): Map<IntArray, Int> {
            val r: MutableMap<IntArray, Int> = HashMap()
            for (i in 0 until sequences.size) {
                r[intArray(sequences[i])] = i
            }
            return r
        }

        private fun intArray(s: String): IntArray {
            return intArray(bytes(s))
        }

        private fun intArray(byteArray: ByteArray): IntArray {
            val r = IntArray(byteArray.size)
            for (i in byteArray.indices) {
                r[i] = byteArray[i].toInt()
            }
            return r
        }

        private fun recognizer( //
            s: String, sequencesTree: ByteNode<Int>
        ): ByteSequenceRecognizer<Int> {
            return ByteSequenceRecognizer(stream(s), sequencesTree)
        }

        private fun stream(s: String): InputStream {
            return ByteArrayInputStream(bytes(s))
        }

        private fun bytes(s: String): ByteArray {
            return s.toByteArray(TEST_CHARSET)
        }
    }
}