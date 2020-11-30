package io.github.saneea.dvh.feature.time.format;

import java.time.ZonedDateTime;

public interface Format {

	ZonedDateTime parse(String time);

	String render(ZonedDateTime time);

}
