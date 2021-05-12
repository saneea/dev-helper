package io.github.saneea.dvh.utils

import java.io.Closeable
import java.io.InputStream
import java.io.PushbackInputStream

class ByteSequenceRecognizer<T>(stream: InputStream, sequencesTree: ByteNode<T>) : Closeable {

    interface ByteNode<T> {
        fun getNextNode(byteCode: Int): ByteNode<T>?
        val result: T?
        val depth: Int
    }

    val stream = PushbackInputStream(stream, sequencesTree.depth)

    val result = detectSequence(this.stream, sequencesTree)

    override fun close() = stream.close()
}

private fun <T> detectSequence(stream: PushbackInputStream, sequencesTree: ByteSequenceRecognizer.ByteNode<T>?): T? {
    if (sequencesTree == null) {
        return null
    }

    val result: T? = sequencesTree.result
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

fun <T> Map<IntArray, T>.byteNode(): ByteSequenceRecognizer.ByteNode<T> {
    val root = ByteNodeImpl<T>()
    this.forEach { (bomBytes: IntArray, charset: T) ->
        buildBranch(root, bomBytes, 0, charset)
    }
    return root
}

private fun <T> buildBranch(
    sequencesTree: ByteNodeImpl<T>,
    sequenceBytes: IntArray,
    sequenceBytesOffset: Int,
    result: T
) {
    if (sequenceBytesOffset >= sequenceBytes.size) {
        sequencesTree.result = result
    } else {
        buildBranch(
            sequencesTree.getNextOrCreate(sequenceBytes[sequenceBytesOffset]),
            sequenceBytes, sequenceBytesOffset + 1,
            result
        )
    }
}

private class ByteNodeImpl<T> : ByteSequenceRecognizer.ByteNode<T> {
    private val nextNodes: MutableMap<Int, ByteNodeImpl<T>> = HashMap()

    override var result: T? = null

    fun getNextOrCreate(byteCode: Int): ByteNodeImpl<T> {
        var nextNode = nextNodes[byteCode]
        if (nextNode == null) {
            nextNode = ByteNodeImpl()
            nextNodes[byteCode] = nextNode
        }
        return nextNode
    }

    override fun getNextNode(byteCode: Int) = nextNodes[byteCode]

    override val depth: Int
        get() = nextNodes.values
            .map(ByteNodeImpl<T>::depth)
            .map(1::plus)
            .maxOrNull() ?: 0
}
