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
			' ', '.', '*', 'o', //
			'+', 'X', '#', '$', //
			'%', '@', '!', '-', //
			'+', '~', '(', '}', //
			'"', 'H' };

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
		run(in, out);
	}

	public void run(InputStream input, PrintStream out) throws IOException {
		int byteCode;
		while ((byteCode = input.read()) != -1) {
			handleInputByte(byteCode);
		}

		out.print('+');
		for (int col = 0; col < PICTURE_SIZE_X; ++col) {
			out.print('-');
		}
		out.print('+');
		out.println();

		for (Picture.Row row : picture.rows) {
			out.print('|');
			for (int element : row.elements) {
				out.print(intToAsciiPixel(element));
			}
			out.print('|');
			out.println();
		}

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
		for (int i = 0; i < 4; ++i) {
			boolean directionX = (byteCode % 2) != 0;
			byteCode >>= 1;
			boolean directionY = (byteCode % 2) != 0;
			byteCode >>= 1;

			handleDirection(directionX, directionY);
		}
	}

	private void handleDirection(boolean directionX, boolean directionY) {

		currentX = changeCurrentPos(currentX, directionX, PICTURE_SIZE_X);
		currentY = changeCurrentPos(currentY, directionY, PICTURE_SIZE_Y);

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
