package io.github.saneea.feature;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import io.github.saneea.Feature;
import io.github.saneea.FeatureContext;

public class FromHex implements Feature {

	private static final int HEX_DIGITS_IN_BYTE = 2;

	@Override
	public void run(FeatureContext context) throws IOException {
		try (Reader reader = new InputStreamReader(context.getIn())) {

			OutputStream output = context.getOut();

			while (true) {

				List<Character> buf = readCharsFromStream(reader);

				if (buf == null || buf.size() < HEX_DIGITS_IN_BYTE) {
					break;
				}

				int digit0 = Character.digit(buf.get(0), 16);
				int digit1 = Character.digit(buf.get(1), 16);

				int byteCode = ((digit0 << 4) + digit1);

				output.write(byteCode);
			}
		}

	}

	private List<Character> readCharsFromStream(Reader reader) throws IOException {

		List<Character> outChars = new ArrayList<>(HEX_DIGITS_IN_BYTE);

		char[] tempBuf = new char[HEX_DIGITS_IN_BYTE];

		while (outChars.size() < HEX_DIGITS_IN_BYTE) {
			int len = reader.read(tempBuf);
			if (len == -1) {
				return null;
			}

			for (int i = 0; i < len; ++i) {
				outChars.add(tempBuf[i]);
			}
		}

		return outChars;
	}

}
