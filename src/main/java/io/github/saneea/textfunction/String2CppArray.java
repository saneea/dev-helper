package io.github.saneea.textfunction;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class String2CppArray implements TextFunction {

	@Override
	public String apply(String s) {

		return IntStream.concat(s.chars(), IntStream.of(0))//
				.mapToObj(charCode -> wrapIfEscaped((char) charCode))//
				.map(this::wrapInQuotes)//
				.collect(Collectors.joining(","));
	}

	private String wrapInQuotes(String s) {
		return "'" + s + "'";
	}

	private String wrapIfEscaped(char c) {
		switch (c) {
		case '\0':
			return "\\0";

		case '\'':
		case '\\':
			return "\\" + c;

		default:
			return "" + c;
		}
	}

}
