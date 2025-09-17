# Pip User Guide

<img width="524" height="886" alt="Ui" src="https://github.com/user-attachments/assets/d3d3565b-540a-44fe-b28c-8583beb2f029" />

## Getting started 🚀
1. Download the latest JAR from GitHub Releases
2. Open a terminal in the folder containing the JAR.
3. Run java -jar pip.jar, then type todo CS2103T Quiz and press Enter.

## Why Pip
- **Cozy [Animal Crossing](https://animalcrossing.nintendo.com/new-horizons/) vibes**
- Fast text commands 
- Flexible date/time parsing 
- Fuzzy search with minor-typo tolerance 
- Automatic save/load
> 💡 Tip: Use find `<keywords>` to quickly filter tasks. It’s case-insensitive and tolerates small typos.

## Quick actions (task list)
- [x] Add a todo: `todo Make 2101 presentation slides`
- [ ] Add a deadline: `deadline 2103 Quiz /by 2/10/2025 1600`
- [ ] Search tasks: `find quiz`
- [ ] Exit: `bye`

## Adding todos
**Action & outcome:** Create a simple task. Pip confirms the add and saves automatically.<br>
**Usage:** `todo <description>`<br>
**Example:** `todo Make 2101 presentation slides`<br>
**Expected outcome:**
```java
Got it. I've added this task:
[T][ ] Make 2101 presentation slides
Now you have 1 tasks in the list.
```

## Adding deadlines
**Action & outcome:** Create a task with a due date/time. Pip parses flexible date formats, confirms the add, and saves.<br>
**Usage:** `deadline <description> /by <date-or-datetime>`<br>
**Example:** `deadline 2103 Quiz /by 2/10/2025 1600`<br>
**Expected outcome:**
```
Got it. I've added this task:
  [D][ ] 2103 Quiz (by: Sep 2 2025, 4:00 PM)
Now you have 2 tasks in the list.
```

## Adding events
**Action & outcome:** Create an event with a start and end. Pip confirms the add and saves.<br>
**Usage:** `event <description> /from <start> /to <end>`<br>
**Example:** `event Camp /from Mon 9am /to Wed 5pm`<br>
**Expected outcome:**
```
Got it. I've added this task:
  [E][ ] Camp (from: Mon 9am to: Wed 5pm)
Now you have 3 tasks in the list.
```

## Listing tasks
**Action & outcome:** Show all tasks in order. If there are none, Pip shows a message.<br>
**Usage:** `list`<br>
**Expected outcome:**<br>
```
1. [T][ ] Make 2101 presentation slides
2. [D][ ] 2103 Quiz (by: Sep 2 2025, 4:00 PM)
3. [E][ ] Camp (from: Mon 9am to: Wed 5pm)
```

## Finding tasks
**Action & outcome:** Search task descriptions using one or more keywords (case-insensitive). Each term must match; minor typos (edit distance ≤ 1 per term) are tolerated.<br>
**Usage:** `find <keyword(s)>`<br>
**Example:** `find canp     # matches "camp" (one-character typo)`<br>
**Expected outcome:**
```
Here are the matching tasks in your list:
1. [E][ ] Camp (from: Mon 9am to: Wed 5pm)
```

## Marking task
**Action & outcome:** Mark a task (by its list index) as done. Pip updates the task and saves.<br>
**Usage:** `mark <index>`<br>
**Example:** `mark 3`<br>
**Expected outcome:**
```
Nice! I've marked this task as done:
  [E][X] Camp (from: Mon 9am to: Wed 5pm)
```

## Unmarking task
**Action & outcome:** Mark a task (by its list index) as not done. Pip updates the task and saves.<br>
**Usage:** `unmark <index>`<br>
**Example:** `unmark 3`<br>
**Expected outcome:**
```
OK, I've marked this task as not done yet:
  [E][ ] Camp (from: Mon 9am to: Wed 5pm)
```

## Deleting task
**Action & outcome:** Remove a task (by its list index). Pip confirms removal and saves.<br>
**Usage:** `delete <index>`<br>
**Example:** `delete 3`<br>
**Expected outcome:**
```
Noted. I've removed this task:
  [E][ ] Camp (from: Mon 9am to: Wed 5pm)
Now you have 2 tasks in the list.
```

## Exiting
**Action & outcome:** Close Pip.<br>
**Usage:** `bye`<br>
