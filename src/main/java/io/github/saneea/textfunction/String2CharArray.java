package io.github.saneea.textfunction;

import java.util.stream.Collectors;

public class String2CharArray implements TextFunction {

	@Override
	public String apply(String s) {

		return s.chars()//
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
