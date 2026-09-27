import exceptions.BookNotAvailableException;
import exceptions.InvalidOperationException;
import exceptions.MemberNotFoundException;
import library.Library;
import models.Book;
import models.FacultyMember;
import models.Member;
import models.StudentMember;
import models.Transaction;

import java.util.List;
import java.util.Scanner;

/**
 * Console entry point. Presents a menu-driven interface so you can
 * add books, register members, borrow/return books, and see overdue fines.
 * Data is loaded from /data on startup and saved back on every change.
 */
public class Main {
    private static Library library = new Library();
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        library.loadFromDisk();
        seedSampleDataIfEmpty();

        boolean running = true;
        while (running) {
            printMenu();
            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1": addBook(); break;
                case "2": listBooks(); break;
                case "3": searchBooks(); break;
                case "4": registerMember(); break;
                case "5": listMembers(); break;
                case "6": borrowBook(); break;
                case "7": returnBook(); break;
                case "8": showOverdue(); break;
                case "9": showAllTransactions(); break;
                case "0":
                    running = false;
                    library.saveToDisk();
                    System.out.println("Data saved. Goodbye!");
                    break;
                default:
                    System.out.println("Invalid option, try again.");
            }
        }
    }

    private static void printMenu() {
        System.out.println("\n===== LIBRARY MANAGEMENT SYSTEM =====");
        System.out.println("1. Add a book");
        System.out.println("2. List all books");
        System.out.println("3. Search books (title/author)");
        System.out.println("4. Register a member");
        System.out.println("5. List all members");
        System.out.println("6. Borrow a book");
        System.out.println("7. Return a book");
        System.out.println("8. Show overdue books");
        System.out.println("9. Show transaction history");
        System.out.println("0. Save & Exit");
        System.out.print("Choose an option: ");
    }

    private static void addBook() {
        try {
            System.out.print("ISBN: ");
            String isbn = scanner.nextLine().trim();
            System.out.print("Title: ");
            String title = scanner.nextLine().trim();
            System.out.print("Author: ");
            String author = scanner.nextLine().trim();
            System.out.print("Genre: ");
            String genre = scanner.nextLine().trim();
            System.out.print("Number of copies: ");
            int copies = Integer.parseInt(scanner.nextLine().trim());

            library.addBook(new Book(isbn, title, author, genre, copies));
            library.saveToDisk();
            System.out.println("Book added successfully.");
        } catch (NumberFormatException e) {
            System.out.println("Error: copies must be a number.");
        } catch (InvalidOperationException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void listBooks() {
        List<Book> allBooks = library.getAllBooks().stream().toList();
        if (allBooks.isEmpty()) {
            System.out.println("No books in the library yet.");
            return;
        }
        System.out.println("\nISBN       | Title                          | Author               | Genre           | Availability");
        for (Book b : allBooks) {
            System.out.println(b);
        }
    }

    private static void searchBooks() {
        System.out.print("Search by (1) Title or (2) Author: ");
        String mode = scanner.nextLine().trim();
        System.out.print("Enter keyword: ");
        String keyword = scanner.nextLine().trim();

        List<Book> results = mode.equals("2")
                ? library.searchByAuthor(keyword)
                : library.searchByTitle(keyword);

        if (results.isEmpty()) {
            System.out.println("No matching books found.");
        } else {
            results.forEach(System.out::println);
        }
    }

    private static void registerMember() {
        try {
            System.out.print("Member ID: ");
            String id = scanner.nextLine().trim();
            System.out.print("Name: ");
            String name = scanner.nextLine().trim();
            System.out.print("Email: ");
            String email = scanner.nextLine().trim();
            System.out.print("Type - (1) Student or (2) Faculty: ");
            String type = scanner.nextLine().trim();

            Member member = type.equals("2")
                    ? new FacultyMember(id, name, email)
                    : new StudentMember(id, name, email);

            library.registerMember(member);
            library.saveToDisk();
            System.out.println("Member registered successfully.");
        } catch (InvalidOperationException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void listMembers() {
        List<Member> allMembers = library.getAllMembers().stream().toList();
        if (allMembers.isEmpty()) {
            System.out.println("No members registered yet.");
            return;
        }
        allMembers.forEach(System.out::println);
    }

    private static void borrowBook() {
        try {
            System.out.print("Member ID: ");
            String memberId = scanner.nextLine().trim();
            System.out.print("Book ISBN: ");
            String isbn = scanner.nextLine().trim();

            Transaction t = library.borrowBook(memberId, isbn);
            library.saveToDisk();
            System.out.println("Book borrowed successfully. Due date: " + t.getDueDate());
        } catch (MemberNotFoundException | BookNotAvailableException | InvalidOperationException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void returnBook() {
        try {
            System.out.print("Member ID: ");
            String memberId = scanner.nextLine().trim();
            System.out.print("Book ISBN: ");
            String isbn = scanner.nextLine().trim();

            double fine = library.returnBook(memberId, isbn);
            library.saveToDisk();
            if (fine > 0) {
                System.out.printf("Book returned. Late fine due: %.2f%n", fine);
            } else {
                System.out.println("Book returned on time. No fine.");
            }
        } catch (MemberNotFoundException | InvalidOperationException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void showOverdue() {
        List<Transaction> overdue = library.getOverdueTransactions();
        if (overdue.isEmpty()) {
            System.out.println("No overdue books. Nice!");
        } else {
            System.out.println("Overdue transactions:");
            overdue.forEach(System.out::println);
        }
    }

    private static void showAllTransactions() {
        List<Transaction> all = library.getAllTransactions();
        if (all.isEmpty()) {
            System.out.println("No transactions yet.");
        } else {
            all.forEach(System.out::println);
        }
    }

    /** Adds a couple of sample records the first time you run the app, so the menu isn't empty. */
    private static void seedSampleDataIfEmpty() {
        if (library.getAllBooks().isEmpty()) {
            try {
                library.addBook(new Book("978-0132350884", "Clean Code", "Robert C. Martin", "Programming", 3));
                library.addBook(new Book("978-0201633610", "Design Patterns", "Gang of Four", "Programming", 2));
                library.addBook(new Book("978-0134685991", "Effective Java", "Joshua Bloch", "Programming", 2));
            } catch (InvalidOperationException ignored) {}
        }
        if (library.getAllMembers().isEmpty()) {
            try {
                library.registerMember(new StudentMember("S001", "Asha Rao", "asha@example.com"));
                library.registerMember(new FacultyMember("F001", "Dr. Mehta", "mehta@example.com"));
            } catch (InvalidOperationException ignored) {}
        }
    }
}
