package pip.logic;

import pip.app.PipException;
import pip.model.Task;
import pip.model.TaskList;
import pip.storage.Storage;
import pip.ui.Ui;

/**
 * Finds tasks whose descriptions match a keyword (or set of keywords).
 * Matching is case-insensitive and tolerant to minor typos:
 * a task matches if its description contains each term or if it contains
 * a substring within edit distance ≤ 1 of each term.
 */
public class FindTasks extends Command {
    private final String keyword;

    /**
     * Constructs a {@code FindTasks} command.
     *
     * @param args raw keyword(s) (may contain spaces). Leading/trailing whitespace is trimmed.
     */
    public FindTasks(String args) {
        this.keyword = args == null ? "" : args.trim();
    }

    /**
     * Searches the {@link TaskList} for tasks matching all provided terms (split on whitespace),
     * then displays the results or a "no matches" message.
     *
     * @param tasks   the task list to search; must not be {@code null}
     * @param ui      the UI facade for presenting results; must not be {@code null}
     * @param storage the persistence layer (unused by this command)
     * @throws PipException if no keyword was supplied (usage error)
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws PipException {
        if (keyword.isEmpty()) {
            throw new PipException("Usage: find <keyword>");
        }

        String[] kw = keyword.toLowerCase().split("\\s+");
        var all = tasks.asList();

        StringBuilder sb = new StringBuilder("Here are the matching tasks in your list:\n");
        int count = 0;
        for (Task t : all) {
            String desc = t.getDescription().toLowerCase();
            if (matchesAll(desc, kw)) {
                count++;
                sb.append(count).append(". ").append(t).append("\n");
            }
        }

        if (count == 0) {
            ui.show("No matching tasks found for: " + keyword);
        } else {
            ui.show(sb.toString().trim());
        }
    }

    /**
     * Returns {@code true} if the description matches <em>all</em> terms.
     *
     * @param desc  the lower-cased task description
     * @param terms the set of lower-cased terms
     */
    private static boolean matchesAll(String desc, String[] terms) {
        for (String term : terms) {
            if (!matchesOne(desc, term)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns {@code true} if the description contains the term or contains a
     * near match (edit distance ≤ 1) to the term.
     *
     * @param desc  the lower-cased task description
     * @param term  a single lower-cased search term
     */
    private static boolean matchesOne(String desc, String term) {
        if (term.isEmpty()) {
            return true;
        }
        if (desc.contains(term)) {
            return true;
        }
        return containsWithEditDistanceAtMostOne(desc, term);
    }

    /**
     * Returns {@code true} if any substring of {@code hay} is within edit distance ≤ 1 of {@code needle}.
     */
    private static boolean containsWithEditDistanceAtMostOne(String hay, String needle) {
        int n = needle.length();
        if (n == 0) {
            return true;
        }
        if (hay.contains(needle)) {
            return true;
        }
        if (n > hay.length() + 1) {
            return false;
        }
        for (int i = 0; i <= hay.length() - Math.max(1, n - 1); i++) {
            int end = Math.min(hay.length(), i + n + 1); // allow one extra char window
            if (editDistanceAtMostOne(hay.substring(i, end), needle)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns {@code true} if the Levenshtein edit distance between {@code a} and {@code b} is ≤ 1.
     * Permits at most one insertion, deletion, or substitution.
     */
    private static boolean editDistanceAtMostOne(String a, String b) {
        int la = a.length();
        int lb = b.length();
        if (Math.abs(la - lb) > 1) {
            return false;
        }
        int i = 0;
        int j = 0;
        int edits = 0;
        while (i < la && j < lb) {
            if (a.charAt(i) == b.charAt(j)) {
                i++;
                j++;
                continue;
            }
            if (++edits > 1) {
                return false;
            }
            if (la == lb) {
                i++;
                j++;
            } else if (la > lb) {
                i++;
            } else {
                j++;
            }
        }
        return edits + (la - i) + (lb - j) <= 1;
    }
}
