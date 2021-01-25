package io.github.saneea.dvh.utils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import io.github.saneea.dvh.utils.ByteSequenceRecognizer.ByteNode;

public class EncodingRecognizer {

	private static final Map<int[], Charset> BOM_TO_CHARSET = Map.of(//
			new int[] { 0xFE, 0xFF }, StandardCharsets.UTF_16BE, //
			new int[] { 0xFF, 0xFE }, StandardCharsets.UTF_16LE, //
			new int[] { 0xEF, 0xBB, 0xBF }, StandardCharsets.UTF_8 //
	);

	private static final ByteNode<Charset> BOM_TREE = ByteNode.fromMap(BOM_TO_CHARSET);

	public static ByteSequenceRecognizer<Charset> recognize(//
			InputStream inBinStream) throws IOException {
		return new ByteSequenceRecognizer<>(inBinStream, BOM_TREE);
	}

}
