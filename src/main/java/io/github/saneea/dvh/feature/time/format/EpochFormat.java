package io.github.saneea.dvh.feature.time.format;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.function.LongFunction;
import java.util.function.ToLongFunction;

public class EpochFormat implements Format {

	private final LongFunction<Instant> epochToInstant
	// = Instant::ofEpochMilli
	;
	private final ToLongFunction<Instant> instantToEpoch
	// = Instant::toEpochMilli
	;

	public EpochFormat(LongFunction<Instant> epochToInstant, ToLongFunction<Instant> instantToEpoch) {
		this.epochToInstant = epochToInstant;
		this.instantToEpoch = instantToEpoch;
	}

	@Override
	public ZonedDateTime parse(String timeAsString) {
		long timeAsNumber = Long.valueOf(timeAsString);
		Instant timeAsInstant = epochToInstant.apply(timeAsNumber);
		ZonedDateTime timeAsZoned = ZonedDateTime.ofInstant(timeAsInstant, ZoneId.systemDefault());
		return timeAsZoned;
	}

	@Override
	public String render(ZonedDateTime timeAsZoned) {
		Instant timeAsInstant = timeAsZoned.toInstant();
		long timeAsNumber = instantToEpoch.applyAsLong(timeAsInstant);
		String timeAsString = String.valueOf(timeAsNumber);
		return timeAsString;
	}

}
