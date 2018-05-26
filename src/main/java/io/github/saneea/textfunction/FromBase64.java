package io.github.saneea.textfunction;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Base64;

public class FromBase64 {

	public static void main(String[] args) throws IOException {
		try (InputStream in = Base64.getDecoder().wrap(new BufferedInputStream(System.in)); //
				OutputStream out = new BufferedOutputStream(System.out)) {
			Utils.transferData(in, out);
		}
	}

}
