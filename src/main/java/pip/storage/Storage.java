package pip.storage;

import pip.app.PipException;
import pip.model.Task;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

/**
 * File-backed storage for loading and saving Pip tasks.
 * Creates the data directory/file on demand and reads/writes lines in the
 * pipe-delimited format produced by pip.model.Task#toDataString().
 */
public class Storage {
    private final Path dataDir;
    private final Path dataFile;

    /**
     * Constructs a Storage instance for the given file path.
     *
     * @param filePath Path to the persistent tasks file.
     */
    public Storage(String filePath) {
        this.dataFile = Paths.get(filePath);
        this.dataDir = dataFile.getParent() != null ? dataFile.getParent() : Paths.get(".");
    }

    /**
     * Loads tasks from disk.
     * If the directory/file does not exist, they are created and an empty list is returned.
     *
     * @return A list of deserialized tasks; empty if the file was newly created or empty.
     * @throws PipException If the file cannot be read or a line is malformed.
     */
    public List<Task> load() throws PipException {
        List<Task> out = new ArrayList<>();
        try {
            if (Files.notExists(dataDir)) Files.createDirectories(dataDir);
            if (Files.notExists(dataFile)) {
                Files.createFile(dataFile);
                return out; // nothing to load yet
            }
            List<String> lines = Files.readAllLines(dataFile, StandardCharsets.UTF_8);
            for (String line : lines) {
                String trimmed = line.trim();
                if (trimmed.isEmpty()) continue;
                out.add(Task.fromDataString(trimmed));
            }
            return out;
        } catch (IOException e) {
            throw new PipException("Failed to read save file.");
        }
    }

    /**
     * Saves the given tasks to disk, replacing the existing contents.
     *
     * @param items Tasks to persist, in the same order they should appear in the file.
     * @throws PipException If writing fails for any reason.
     */
    public void save(List<Task> items) throws PipException {
        try {
            if (Files.notExists(dataDir)) Files.createDirectories(dataDir);
            List<String> lines = new ArrayList<>();
            for (Task t : items) lines.add(t.toDataString());
            Files.write(
                    dataFile,
                    lines,
                    StandardCharsets.UTF_8,
                    StandardOpenOption.TRUNCATE_EXISTING,
                    StandardOpenOption.CREATE
            );
        } catch (IOException e) {
            throw new PipException("Failed to save tasks to disk.");
        }
    }
}
