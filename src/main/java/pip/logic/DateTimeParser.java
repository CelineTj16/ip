package pip.logic;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Locale;

import pip.app.PipException;

/**
 * Utility for parsing and formatting date/time values used by Pip.
 * Accepts multiple human-friendly input formats and produces consistent display strings.
 */
public class DateTimeParser {
    /**
     * Parses a variety of date/time strings into a LocalDateTime.
     *
     * @param s Input string to parse (leading/trailing spaces allowed).
     * @return Parsed date-time.
     * @throws PipException If the input does not match any supported format.
     */
    @SuppressWarnings("checkstyle:SeparatorWrap")
    public static LocalDateTime parseDateTimeFlexible(String s) throws PipException {
        String input = s.trim();

        try {
            return LocalDateTime.parse(input);
        } catch (DateTimeParseException ignored) {
            // fall through to other formats
        }
        try {
            return LocalDate.parse(input).atStartOfDay();
        } catch (DateTimeParseException ignored) {
            // continue
        }

        DateTimeFormatter[] candidates = new DateTimeFormatter[] {
                builder("d/M/yyyy HHmm"),
                builder("d/M/yyyy H:mm"),
                builder("d/M/yyyy h:mma"),
                builder("d/M/yyyy ha"),
                builder("d/M/yyyy"),

                builder("d-M-yyyy HHmm"),
                builder("d-M-yyyy H:mm"),
                builder("d-M-yyyy h:mma"),
                builder("d-M-yyyy ha"),
                builder("d-M-yyyy"),
        };

        for (DateTimeFormatter f: candidates) {
            try {
                String pat = f.toString();
                boolean isSlashDate = pat.contains("d/M/yyyy");
                boolean isDashDate = pat.contains("d-M-yyyy");
                if ((isSlashDate && input.matches("\\d{1,2}/\\d{1,2}/\\d{4}$"))
                        || (isDashDate && input.matches("\\d{1,2}-\\d{1,2}-\\d{4}$"))) {
                    return LocalDate.parse(input, f).atStartOfDay();
                }
                return LocalDateTime.parse(input, f);
            } catch (DateTimeParseException ignored) {
                // try next formatter
            }
        }

        throw new PipException("Invalid date/time. Examples: "
                + "2019-12-02, 2/12/2019 1800, 2/12/2019 6:15pm, 2019-12-02T18:00");
    }

    /**
     * Formats a LocalDateTime for compact, friendly display.
     * If the time portion is midnight, returns {@code "MMM d yyyy"}.
     * Otherwise, returns {@code "MMM d yyyy, h:mm a"}.
     *
     * @param dt Date-time to format.
     * @return Human-friendly string for UI.
     */
    public static String formatDateTimeSmart(LocalDateTime dt) {
        if (dt.toLocalTime().equals(LocalTime.MIDNIGHT)) {
            return dt.format(DateTimeFormatter.ofPattern("MMM d yyyy"));
        }
        return dt.format(DateTimeFormatter.ofPattern("MMM d yyyy, h:mm a"));
    }

    private static DateTimeFormatter builder(String pattern) {
        return new DateTimeFormatterBuilder()
                .parseCaseInsensitive()
                .appendPattern(pattern)
                .toFormatter(Locale.ENGLISH)
                .withResolverStyle(ResolverStyle.SMART);
    }
}
