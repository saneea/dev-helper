package io.github.saneea.dvh.utils

import java.io.Closeable
import java.io.IOException
import java.io.InputStream
import java.io.PushbackInputStream

class ByteSequenceRecognizer<T>(stream: InputStream?, sequencesTree: ByteNode<T>) : Closeable {

    interface ByteNode<T> {
        fun getNextNode(byteCode: Int): ByteNode<T>?
        fun getResult(): T?
        val depth: Int

        companion object {
            @JvmStatic
            fun <T> fromMap(sequencesMap: Map<IntArray, T>): ByteNode<T> {
                val root = ByteNodeImpl<T>()
                sequencesMap.forEach { (bomBytes: IntArray, charset: T) ->
                    buildBranch(root, bomBytes, 0, charset)
                }
                return root
            }

            private fun <T> buildBranch(
                sequencesTree: ByteNodeImpl<T>,
                sequenceBytes: IntArray, sequenceBytesOffset: Int, result: T
            ) {
                if (sequenceBytesOffset < sequenceBytes.size) {
                    buildBranch<T>(
                        sequencesTree.getNextOrCreate(sequenceBytes[sequenceBytesOffset]),
                        sequenceBytes, sequenceBytesOffset + 1,
                        result
                    )
                } else {
                    sequencesTree._result = result
                }
            }
        }
    }

    private val stream: PushbackInputStream = PushbackInputStream(stream, sequencesTree.depth)

    private val result: T? = detectSequence(this.stream, sequencesTree)
    fun stream(): InputStream {
        return stream
    }

    fun result(): T? {
        return result
    }

    @Throws(IOException::class)
    override fun close() {
        stream.close()
    }

    private class ByteNodeImpl<T> : ByteNode<T> {
        private val nextNodes: MutableMap<Int, ByteNodeImpl<T>> = HashMap()

        var _result: T? = null

        override fun getResult(): T? {
            return _result
        }

        fun getNextOrCreate(byteCode: Int): ByteNodeImpl<T> {
            var nextNode = nextNodes[byteCode]
            if (nextNode == null) {
                nextNode = ByteNodeImpl()
                nextNodes[byteCode] = nextNode
            }
            return nextNode
        }

        override fun getNextNode(byteCode: Int): ByteNode<T>? {
            return nextNodes[byteCode]
        }

        override val depth: Int
            get() = nextNodes
                .values.stream()
                .mapToInt { obj: ByteNodeImpl<T> -> obj.depth }
                .map { childDepth: Int -> childDepth + 1 }
                .max().orElse(0)
    }

    companion object {
        @Throws(IOException::class)
        private fun <T> detectSequence(stream: PushbackInputStream, sequencesTree: ByteNode<T>?): T? {
            if (sequencesTree == null) {
                return null
            }
            val result: T? = sequencesTree.getResult()
            if (result != null) {
                return result
            }
            val byteCode = stream.read()
            return if (byteCode < 0) {
                null
            } else try {
                detectSequence(stream, sequencesTree.getNextNode(byteCode))
            } finally {
                stream.unread(byteCode)
            }
        }
    }

}