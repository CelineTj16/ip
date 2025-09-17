package pip.logic;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.time.temporal.TemporalAccessor;
import java.util.Locale;

import pip.app.PipException;

/**
 * Parses and formats date/time strings for Pip.
 * Supports ISO formats and common d/M/yyyy or d-M-yyyy variants.
 */
public class DateTimeParser {

    private static final String INVALID_HINT =
            "Invalid date/time. Examples: 2019-12-02, 2/12/2019 1800, 2/12/2019 6:15pm, 2019-12-02T18:00";

    private static final String[] PATTERNS = {
        "d/M/yyyy HHmm", "d/M/yyyy H:mm", "d/M/yyyy h:mma", "d/M/yyyy ha", "d/M/yyyy",
        "d-M-yyyy HHmm", "d-M-yyyy H:mm", "d-M-yyyy h:mma", "d-M-yyyy ha", "d-M-yyyy"
    };

    /**
     * Parses a variety of date/time strings into a LocalDateTime.
     *
     * @param s input string
     * @return parsed LocalDateTime
     * @throws PipException if no supported pattern matches
     */
    public static LocalDateTime parseDateTimeFlexible(String s) throws PipException {
        String input = s == null ? "" : s.trim();
        if (input.isEmpty()) {
            throw new PipException(INVALID_HINT);
        }

        LocalDateTime dt = tryParseIso(input);
        if (dt != null) {
            return dt;
        }

        dt = tryParseWithPatterns(input);
        if (dt != null) {
            return dt;
        }

        throw new PipException(INVALID_HINT);
    }

    /**
     * Formats a LocalDateTime in a compact, friendly form.
     *
     * @param dt date-time to format
     * @return formatted string
     */
    public static String formatDateTimeSmart(LocalDateTime dt) {
        if (dt.toLocalTime().equals(LocalTime.MIDNIGHT)) {
            return dt.format(DateTimeFormatter.ofPattern("MMM d yyyy"));
        }
        return dt.format(DateTimeFormatter.ofPattern("MMM d yyyy, h:mm a"));
    }

    /**
     * Attempts ISO parsing for both date-time and date-only inputs.
     *
     * @param input input string
     * @return LocalDateTime or null if not ISO
     */
    private static LocalDateTime tryParseIso(String input) {
        try {
            TemporalAccessor ta = DateTimeFormatter.ISO_LOCAL_DATE_TIME
                    .parseBest(input, LocalDateTime::from, LocalDate::from);
            return (ta instanceof LocalDateTime)
                    ? (LocalDateTime) ta
                    : ((LocalDate) ta).atStartOfDay();
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    /**
     * Attempts parsing with supported non-ISO patterns.
     *
     * @param input input string
     * @return LocalDateTime or null if none match
     */
    private static LocalDateTime tryParseWithPatterns(String input) {
        DateTimeParseException last = null;
        for (String p : PATTERNS) {
            DateTimeFormatter f = formatter(p);
            try {
                TemporalAccessor ta = f.parseBest(input, LocalDateTime::from, LocalDate::from);
                return (ta instanceof LocalDateTime)
                        ? (LocalDateTime) ta
                        : ((LocalDate) ta).atStartOfDay();
            } catch (DateTimeParseException e) {
                last = e;
            }
        }
        return null;
    }

    /**
     * Builds a case-insensitive formatter with SMART resolver.
     *
     * @param pattern pattern string
     * @return formatter
     */
    private static DateTimeFormatter formatter(String pattern) {
        return new DateTimeFormatterBuilder()
                .parseCaseInsensitive()
                .appendPattern(pattern)
                .toFormatter(Locale.ENGLISH)
                .withResolverStyle(ResolverStyle.SMART);
    }
}
