package com.vosmann.miniutils.time;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.ParseException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

// TODO: in progress
/**
 * Represents a time interval of specified precision.
 */
public class Interval {

    public static final ZoneId UTC_ZONE_ID = ZoneId.ofOffset("", ZoneOffset.UTC);
    private final boolean MAKE_PARALLEL_STREAM = false;

    // TODO:
    // pass this in for every interval and build its formatter. Or even better, pass in just a time unit that
    // will represent the precisiona and build the format from that.
    public static final String DAY_GRANULARITY_DATE_FORMAT = "yyyy-MM-dd";
    public static final DateTimeFormatter DAY_FORMATTER = DateTimeFormat.forPattern(DAY_GRANULARITY_DATE_FORMAT);

    private final Instant begin;
    private final Instant end;

    private final ChronoUnit step;

    private final boolean includeBegin;
    private final boolean includeEnd;

    private Interval(Instant begin, Instant end, ChronoUnit step, boolean includeBegin, boolean includeEnd) {
        this.begin = roundTo(begin, step);
        this.end = roundTo(end, step);
        this.step = step;
        this.includeBegin = includeBegin;
        this.includeEnd = includeEnd;
    }

    // Includes begin
    public static Interval startingOn(final Instant begin, final int length, final ChronoUnit step) {
        return null;
    }
    // Does not include begin
    public static Interval startingOnOpen(final Instant begin, final int length, final ChronoUnit step) {
        return null;
    }

    // Includes end
    public static Interval endingOn(final Instant begin, final int length, final ChronoUnit step) {
        return null;
    }
    // Does not include end
    public static Interval endingOnOpen(final Instant begin, final int length, final ChronoUnit step) {
        return null;
    }

    public static Interval open(final Instant begin, final Instant end, final ChronoUnit step) {
        return new Interval(begin, end, step, false, false);
    }

    public static Interval closed(final Instant begin, final Instant end, final ChronoUnit step) {
        return new Interval(begin, end, step, true, true);
    }

    private Instant roundTo(final Instant instant, final ChronoUnit unit) {
        final ZonedDateTime zoned = ZonedDateTime.ofInstant(instant, UTC_ZONE_ID);
        return zoned.truncatedTo(unit).toInstant();
    }

    // <T> Stream<T> stream(Iterator<T> iterator) {
    public Stream<Instant> get() {
        // Spliterator<T> spliterator = Spliterators.spliteratorUnknownSize(iterator, 0);
        return StreamSupport.stream(null, MAKE_PARALLEL_STREAM);

    }

    /**
     * @return  A closed interval [end-nrDays+1, end] with day-level granularity.
     */
    public static List<DateTime> buildIntervalEndingOn(final DateTime end, final int nrDays) {
        final ImmutableList.Builder<DateTime> interval = ImmutableList.builder();
        for (int dayNr = nrDays - 1; dayNr >= 0; --dayNr) {
            final DateTime day = end.minusDays(dayNr);
            interval.add(day);
        }
        return interval.build();
    }

    // TODO Separate class.
    public static DateTime getDateFromDaysAgo(final int daysAgo) {
        Preconditions.checkArgument(daysAgo >= 0);
        return DateTime.now().minusDays(daysAgo);
    }

    // TODO Separate class.
    public static DateTime fromDayString(final String dayString) throws ParseException {
        return DAY_FORMATTER.parseDateTime(dayString);
    }

    // TODO Separate class.
    public static String toDayString(final DateTime moment) {
        return moment.toString(DAY_FORMATTER);
    }

}
