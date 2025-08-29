import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

public class Storage {
    private static final Path DATA_DIR = Paths.get("data");
    private static final Path DATA_FILE = DATA_DIR.resolve("pip.txt");

    public static void load(ArrayList<Task> out) {
        try {
            if (Files.notExists(DATA_DIR)) {
                Files.createDirectories(DATA_DIR);
            }
            if (Files.notExists(DATA_FILE)) {
                Files.createFile(DATA_FILE);
                return; // nothing to load
            }
            List<String> lines = Files.readAllLines(DATA_FILE, StandardCharsets.UTF_8);
            for (String line : lines) {
                String trimmed = line.trim();
                if (trimmed.isEmpty()) continue;
                try {
                    Task t = Task.fromDataString(trimmed);
                    out.add(t);
                } catch (PipException parseErr) {
                    Pip.chunk("Warning: skipped corrupted line in save file:\n  " + trimmed);
                }
            }
        } catch (IOException e) {
            Pip.chunk("Warning: could not read save file. Continuing without loading.");
        }
    }

    public static void save(ArrayList<Task> items) {
        try {
            if (Files.notExists(DATA_DIR)) {
                Files.createDirectories(DATA_DIR);
            }
            List<String> lines = new ArrayList<>();
            for (Task t : items) {
                lines.add(t.toDataString());
            }
            Files.write(
                    DATA_FILE,
                    lines,
                    StandardCharsets.UTF_8,
                    StandardOpenOption.TRUNCATE_EXISTING,
                    StandardOpenOption.CREATE
            );
        } catch (IOException e) {
            Pip.chunk("Warning: could not save tasks to disk.");
        }
    }
}
