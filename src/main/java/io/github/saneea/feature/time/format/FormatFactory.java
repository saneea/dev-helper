package io.github.saneea.feature.time.format;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FormatFactory {

	public static final String FORMAT_HUMAN = "human";
	private static final String FORMAT_UNIX = "unix";
	private static final String FORMAT_JAVA = "java";
	private static final String FORMAT_ISO = "ISO";

	private final Map<String, Format> formats;

	public FormatFactory() {
		Map<String, Format> f = new LinkedHashMap<>();
		f.put(FORMAT_UNIX, new EpochFormat(Instant::ofEpochSecond, Instant::getEpochSecond));
		f.put(FORMAT_JAVA, new EpochFormat(Instant::ofEpochMilli, Instant::toEpochMilli));
		f.put(FORMAT_HUMAN, new DateTimeFormatterFormat(DateTimeFormatter.RFC_1123_DATE_TIME));
		f.put(FORMAT_ISO, new DateTimeFormatterFormat(DateTimeFormatter.ISO_OFFSET_DATE_TIME));

		formats = Collections.unmodifiableMap(f);
	}

	public Format createFormat(String formatNameOrPattern) {
		Format format = formats.get(formatNameOrPattern);
		return format != null//
				? format//
				: new DateTimeFormatterFormat(//
						DateTimeFormatter//
								.ofPattern(formatNameOrPattern));
	}

	public String getAvailableFormatsString() {
		return Stream//
				.concat(formats.keySet().stream(), //
						Stream.of("<pattern>"))//
				.collect(Collectors.joining("|"));
	}
}
