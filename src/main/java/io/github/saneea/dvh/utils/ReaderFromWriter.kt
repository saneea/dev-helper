package io.github.saneea.dvh.utils

import java.io.IOException
import java.io.Reader
import java.io.Writer
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.BlockingQueue

class ReaderFromWriter(writeCode: (Writer) -> Unit) : Reader() {

    private val q: BlockingQueue<QueueElement> = ArrayBlockingQueue(4096)
    private val proxyWriter = ProxyWriter()
    private var endOfStream = false

    init {
        Thread {
            try {
                writeCode(proxyWriter)
                q.put(EndOfStreamQueueElement())
            } catch (e: Exception) {
                try {
                    q.put(ExceptionQueueElement(e))
                } catch (e1: InterruptedException) {
                    // nothing
                }
            }
        }.start()
    }

    override fun read(cbuf: CharArray, off: Int, len: Int): Int {
        val buffer = CharBufferWrapper(cbuf, off, len)
        while (!endOfStream && !buffer.isEnd) {
            try {
                q.take().accept(buffer)
            } catch (e: Exception) {
                throw IOException(e.toString(), e)
            }
        }
        return if (endOfStream && buffer.currentPos == 0)
            -1
        else
            buffer.currentPos
    }

    override fun close() = Unit

    override fun ready() = !q.isEmpty()

    private class CharBufferWrapper(
        private val cbuf: CharArray,
        private val offset: Int,
        private val size: Int
    ) {
        var currentPos = 0
        fun write(c: Char) {
            cbuf[getPosAndIncrement()] = c
        }

        fun read() = cbuf[getPosAndIncrement()]

        private fun getPosAndIncrement(): Int {
            val ret = currentPos + offset
            ++currentPos
            return ret
        }

        val isEnd
            get() = currentPos >= size
    }

    private inner class ProxyWriter : Writer() {

        override fun write(cbuf: CharArray, off: Int, len: Int) {
            val buffer = CharBufferWrapper(cbuf, off, len)
            while (!buffer.isEnd) {
                try {
                    q.put(CharQueueElement(buffer.read()))
                } catch (e: InterruptedException) {
                    throw IOException(e.toString(), e)
                }
            }
        }

        override fun flush() = Unit

        override fun close() =
            try {
                q.put(EndOfStreamQueueElement())
            } catch (e: InterruptedException) {
                throw IOException(e.toString(), e)
            }
    }

    private interface QueueElement {
        fun accept(buff: CharBufferWrapper)
    }

    private inner class CharQueueElement(val c: Char) : QueueElement {
        override fun accept(buff: CharBufferWrapper) = buff.write(c)
    }

    private inner class EndOfStreamQueueElement : QueueElement {
        override fun accept(buff: CharBufferWrapper) {
            endOfStream = true
        }
    }

    private class ExceptionQueueElement(val e: Exception) : QueueElement {
        override fun accept(buff: CharBufferWrapper) = throw e
    }

}