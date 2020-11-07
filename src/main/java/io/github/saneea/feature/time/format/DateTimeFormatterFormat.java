package io.github.saneea.feature.time.format;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class DateTimeFormatterFormat implements Format {

	private final DateTimeFormatter formatter;

	public DateTimeFormatterFormat(DateTimeFormatter formatter) {
		this.formatter = formatter;
	}

	@Override
	public ZonedDateTime parse(String time) {
		return ZonedDateTime.from(formatter.parse(time));
	}

	@Override
	public String render(ZonedDateTime time) {
		return formatter.format(time);
	}

}
