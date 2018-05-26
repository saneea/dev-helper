package io.github.saneea.textfunction;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Utils {

	public static long transferData(InputStream in, OutputStream out) throws IOException {
		long transfered = 0;
		int wasRead;
		byte[] buff = new byte[4096];
		while ((wasRead = in.read(buff)) != -1) {
			out.write(buff, 0, wasRead);
			transfered += wasRead;
		}
		return transfered;
	}
}
