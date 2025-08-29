import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

public class Storage {
    private final Path dataDir;
    private final Path dataFile;

    public Storage(String filePath) {
        this.dataFile = Paths.get(filePath);
        this.dataDir = dataFile.getParent() != null ? dataFile.getParent() : Paths.get(".");
    }

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
