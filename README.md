# Library Management System (Java, Console App)

A console-based Library Management System built in core Java. It demonstrates
OOP fundamentals, custom exceptions, collections, and file-based persistence —
the kind of project that's good to show for a Java Developer internship.

## Features
- Add books and manage copies (multiple copies per ISBN)
- Search books by title or author
- Register members as either **Student** or **Faculty** (different borrowing rules for each)
- Borrow / return books, with automatic due-date calculation
- Automatic late-fine calculation on return (different rate for students vs faculty)
- View all overdue books and full transaction history
- Data is saved to disk (CSV files in `data/`) and reloaded automatically next run

## OOP Concepts Demonstrated
| Concept | Where |
|---|---|
| Abstraction / Inheritance | `Member` is an abstract class; `StudentMember` and `FacultyMember` extend it with different rules |
| Polymorphism | `getMaxBooksAllowed()`, `getLoanPeriodDays()`, `getFinePerDay()` behave differently per subclass, called through the base `Member` reference |
| Encapsulation | All fields are private with controlled getters/setters |
| Custom Exceptions | `BookNotAvailableException`, `MemberNotFoundException`, `InvalidOperationException` — checked exceptions used for real business rules, not just generic `Exception` |
| Collections | `HashMap` for O(1) book/member lookup by ID, `ArrayList` for transaction history |
| File I/O | Manual CSV read/write for persistence (no external libraries) |
| Java Time API | `LocalDate` / `ChronoUnit` used for due dates and overdue-day calculation |

## Project Structure
```
LibraryManagementSystem/
├── src/
│   ├── Main.java                  # Console menu / entry point
│   ├── models/
│   │   ├── Book.java
│   │   ├── Member.java            # abstract base class
│   │   ├── StudentMember.java
│   │   ├── FacultyMember.java
│   │   └── Transaction.java
│   ├── exceptions/
│   │   ├── BookNotAvailableException.java
│   │   ├── MemberNotFoundException.java
│   │   └── InvalidOperationException.java
│   └── library/
│       └── Library.java           # Core business logic
└── data/                          # Auto-created CSV storage (books, members, transactions)
```

## How to Run

**Requires:** JDK 17 or newer.

```bash
# From inside LibraryManagementSystem/
javac -d bin src/Main.java src/models/*.java src/exceptions/*.java src/library/*.java
java -cp bin Main
```

On first run, the app seeds a few sample books and members so the menu isn't
empty. After that, everything you add is saved to `data/*.csv` and reloaded
automatically the next time you run it.

## Example Session
```
1. Add a book
2. List all books
3. Search books (title/author)
4. Register a member
5. List all members
6. Borrow a book
7. Return a book
8. Show overdue books
9. Show transaction history
0. Save & Exit
Choose an option: 6
Member ID: S001
Book ISBN: 978-0132350884
Book borrowed successfully. Due date: 2026-10-11
```

## Possible Extensions
- Swap CSV storage for a real database (JDBC + MySQL/SQLite) — natural next
  step if you want to practice for backend/Spring Boot roles
- Add a `Reservation` system for books that are fully checked out
- Wrap this logic in a Spring Boot REST API instead of a console menu
- Add JUnit tests for `Library`'s borrow/return/fine logic
