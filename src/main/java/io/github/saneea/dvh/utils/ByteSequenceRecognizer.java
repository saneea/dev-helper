package io.github.saneea.dvh.utils;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ByteSequenceRecognizer<T> implements Closeable {

	public interface ByteNode<T> {
		ByteNode<T> getNextNode(int byteCode);

		T getResult();

		int getDepth();

		static <T> ByteNode<T> fromMap(Map<int[], T> sequencesMap) {
			ByteNodeImpl<T> root = new ByteNodeImpl<T>();

			sequencesMap.forEach((bomBytes, charset) -> //
			buildBranch(root, bomBytes, 0, charset));

			return root;
		}

		private static <T> void buildBranch(ByteNodeImpl<T> sequencesTree, //
				int[] sequenceBytes, int sequenceBytesOffset, T result) {

			if (sequenceBytesOffset < sequenceBytes.length) {
				buildBranch(//
						sequencesTree.getNextOrCreate(sequenceBytes[sequenceBytesOffset]), //
						sequenceBytes, sequenceBytesOffset + 1, //
						result);
			} else {
				sequencesTree.result = result;
			}

		}
	}

	private final PushbackInputStream stream;

	private final Optional<T> result;

	public ByteSequenceRecognizer(InputStream stream, ByteNode<T> sequencesTree) throws IOException {
		this.stream = new PushbackInputStream(stream, sequencesTree.getDepth());
		result = Optional.ofNullable(detectSequence(this.stream, sequencesTree));
	}

	private static <T> T detectSequence(PushbackInputStream stream, ByteNode<T> sequencesTree) throws IOException {

		if (sequencesTree == null) {
			return null;
		}

		T result = sequencesTree.getResult();
		if (result != null) {
			return result;
		}

		int byteCode = stream.read();
		if (byteCode < 0) {
			return null;
		}

		try {
			return detectSequence(stream, sequencesTree.getNextNode(byteCode));
		} finally {
			stream.unread(byteCode);
		}

	}

	public InputStream stream() {
		return stream;
	}

	public Optional<T> result() {
		return result;
	}

	@Override
	public void close() throws IOException {
		stream.close();
	}

	private static class ByteNodeImpl<T> implements ByteNode<T> {
		private final Map<Integer, ByteNodeImpl<T>> nextNodes = new HashMap<>();
		T result;

		ByteNodeImpl<T> getNextOrCreate(int byteCode) {
			ByteNodeImpl<T> nextNode = nextNodes.get(byteCode);
			if (nextNode == null) {
				nextNode = new ByteNodeImpl<>();
				nextNodes.put(byteCode, nextNode);
			}
			return nextNode;
		}

		@Override
		public ByteNode<T> getNextNode(int byteCode) {
			return nextNodes.get(byteCode);
		}

		@Override
		public T getResult() {
			return result;
		}

		@Override
		public int getDepth() {
			return nextNodes//
					.values().stream()//
					.mapToInt(ByteNode::getDepth)//
					.map(childDepth -> childDepth + 1)//
					.max().orElse(0);
		}

	}

}
