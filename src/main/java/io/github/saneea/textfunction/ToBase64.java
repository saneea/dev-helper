package io.github.saneea.textfunction;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Base64;

public class ToBase64 {

	public static void main(String[] args) throws IOException {
		try (InputStream in = new BufferedInputStream(System.in); //
				OutputStream out = Base64.getEncoder().wrap(new BufferedOutputStream(System.out))) {
			Utils.transferData(in, out);
		}
	}

}
