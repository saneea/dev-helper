package io.github.saneea.dvh.feature.binary;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;

import io.github.saneea.dvh.Feature;
import io.github.saneea.dvh.FeatureContext;

public class ToRandomArt implements//
		Feature, //
		Feature.In.Bin.Stream, //
		Feature.Out.Text.PrintStream {

	private static final int PICTURE_SIZE_X = 20;
	private static final int PICTURE_SIZE_Y = 10;

	private static final char[] PICTURE_ALPHABET = { //
			' ', '.', 'o', '+', 'X', '#', 'H'//
	};

	private InputStream in;
	private PrintStream out;

	private final Picture picture = new Picture(PICTURE_SIZE_X, PICTURE_SIZE_Y);

	private int currentX = PICTURE_SIZE_X / 2;
	private int currentY = PICTURE_SIZE_Y / 2;

	@Override
	public Meta meta(FeatureContext context) {
		return Meta.from("convert input binary sequence to random art picture");
	}

	@Override
	public void run(FeatureContext context) throws IOException {
		int byteCode;
		while ((byteCode = in.read()) != -1) {
			handleInputByte(byteCode);
		}

		printHorizontalBorder(out);

		for (Picture.Row row : picture.rows) {
			printVerticalBorder(out);
			for (int element : row.elements) {
				out.print(intToAsciiPixel(element));
			}
			printVerticalBorder(out);
			out.println();
		}

		printHorizontalBorder(out);
	}

	private void printVerticalBorder(PrintStream out) {
		out.print('|');
	}

	private void printHorizontalBorder(PrintStream out) {
		out.print('+');
		for (int col = 0; col < PICTURE_SIZE_X; ++col) {
			out.print('-');
		}
		out.print('+');
		out.println();
	}

	private char intToAsciiPixel(int element) {
		return PICTURE_ALPHABET[element % PICTURE_ALPHABET.length];
	}

	private void handleInputByte(int byteCode) {
		boolean[] bits = byteToBits(byteCode);
		for (int i = 0; i < 4; ++i) {
			handleDirection(bits[i * 2], bits[i * 2 + 1]);
		}
	}

	private boolean[] byteToBits(int byteCode) {
		boolean[] out = new boolean[8];
		for (int i = out.length - 1; i >= 0; --i) {
			out[i] = (byteCode % 2) != 0;
			byteCode >>= 1;
		}
		return out;
	}

	private void handleDirection(boolean horizontal, boolean direction) {

		if (horizontal) {
			currentX = changeCurrentPos(currentX, direction, PICTURE_SIZE_X);
		} else {
			currentY = changeCurrentPos(currentY, direction, PICTURE_SIZE_Y);
		}

		picture.rows[currentY].elements[currentX]++;
	}

	private int changeCurrentPos(int current, boolean direction, int pictureSize) {
		current += (direction ? 1 : -1);
		if (current >= pictureSize) {
			current = 0;
		} else if (current < 0) {
			current = pictureSize - 1;
		}
		return current;
	}

	@Override
	public void setIn(InputStream in) {
		this.in = in;
	}

	@Override
	public void setOut(PrintStream out) {
		this.out = out;
	}

	private static class Picture {

		final Row[] rows;

		public Picture(int sizeX, int sizeY) {
			rows = new Row[sizeY];
			for (int rowIndex = 0; rowIndex < sizeY; ++rowIndex) {
				rows[rowIndex] = new Row(sizeX);
			}
		}

		private static class Row {

			final int[] elements;

			public Row(int size) {
				elements = new int[size];
			}
		}
	}

}
