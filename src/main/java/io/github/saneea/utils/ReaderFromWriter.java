package io.github.saneea.utils;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class ReaderFromWriter extends Reader {

	private final BlockingQueue<QueueElement> q = new ArrayBlockingQueue<>(4096);

	private final ProxyWriter proxyWriter = new ProxyWriter();

	private boolean endOfStream = false;

	public ReaderFromWriter(WriteCode writeCode) {
		new Thread(() -> {
			try {
				writeCode.writeToWriter(proxyWriter);
				q.put(new EndOfStreamQueueElement());
			} catch (Exception e) {
				try {
					q.put(new ExceptionQueueElement(e));
				} catch (InterruptedException e1) {
					// nothing
				}
			}

		}).start();
	}

	@Override
	public int read(char[] cbuf, int off, int len) throws IOException {
		CharBufferWrapper buffer = new CharBufferWrapper(cbuf, off, len);

		while (!endOfStream && !buffer.isEnd()) {
			try {
				q.take().accept(buffer);
			} catch (Exception e) {
				throw new IOException(e.toString(), e);
			}
		}

		return (endOfStream && buffer.currentPos == 0)//
				? -1//
				: buffer.currentPos;
	}

	@Override
	public void close() throws IOException {
	}

	@Override
	public boolean ready() throws IOException {
		return !q.isEmpty();
	}

	@FunctionalInterface
	public interface WriteCode {
		void writeToWriter(Writer writer) throws Exception;
	}

	private static class CharBufferWrapper {
		private char[] cbuf;
		private int offset;
		private int size;

		public int currentPos = 0;

		public CharBufferWrapper(char[] cbuf, int offset, int size) {
			this.cbuf = cbuf;
			this.offset = offset;
			this.size = size;
		}

		public void write(char c) {
			cbuf[getPosAndIncrement()] = c;
		}

		public char read() {
			return cbuf[getPosAndIncrement()];
		}

		private int getPosAndIncrement() {
			int ret = currentPos + offset;
			++currentPos;
			return ret;
		}

		public boolean isEnd() {
			return currentPos >= size;
		}
	}

	private class ProxyWriter extends Writer {

		@Override
		public void write(char[] cbuf, int off, int len) throws IOException {
			CharBufferWrapper buffer = new CharBufferWrapper(cbuf, off, len);
			while (!buffer.isEnd()) {
				try {
					q.put(new CharQueueElement(buffer.read()));
				} catch (InterruptedException e) {
					throw new IOException(e.toString(), e);
				}
			}
		}

		@Override
		public void flush() throws IOException {
		}

		@Override
		public void close() throws IOException {
			try {
				q.put(new EndOfStreamQueueElement());
			} catch (InterruptedException e) {
				throw new IOException(e.toString(), e);
			}
		}

	}

	private interface QueueElement {
		void accept(CharBufferWrapper buff) throws Exception;
	}

	private class CharQueueElement implements QueueElement {
		final char c;

		public CharQueueElement(char c) {
			this.c = c;
		}

		@Override
		public void accept(CharBufferWrapper buff) {
			buff.write(c);
		}
	}

	private class EndOfStreamQueueElement implements QueueElement {

		@Override
		public void accept(CharBufferWrapper buff) {
			endOfStream = true;
		}
	}

	private static class ExceptionQueueElement implements QueueElement {
		final Exception e;

		public ExceptionQueueElement(Exception e) {
			this.e = e;
		}

		@Override
		public void accept(CharBufferWrapper buff) throws Exception {
			throw e;
		}
	}

}
