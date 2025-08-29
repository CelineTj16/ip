package pip.logic;

import pip.app.PipException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Locale;

public class DateTimeParser {
    public static LocalDateTime parseDateTimeFlexible(String s) throws PipException {
        String input = s.trim();

        try {
            return LocalDateTime.parse(input);
        } catch (DateTimeParseException ignored) {}
        try {
            return LocalDate.parse(input).atStartOfDay();
        } catch (DateTimeParseException ignored) {}

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
                boolean isDashDate  = pat.contains("d-M-yyyy");
                if ((isSlashDate && input.matches("\\d{1,2}/\\d{1,2}/\\d{4}$")) ||
                        (isDashDate  && input.matches("\\d{1,2}-\\d{1,2}-\\d{4}$"))) {
                    return LocalDate.parse(input, f).atStartOfDay();
                }
                return LocalDateTime.parse(input, f);
            } catch (DateTimeParseException ignored) {}
        }

        throw new PipException("Invalid date/time. Examples: 2019-12-02, 2/12/2019 1800, 2/12/2019 6:15pm, 2019-12-02T18:00");
    }

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
