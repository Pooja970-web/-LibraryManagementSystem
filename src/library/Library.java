package library;

import exceptions.BookNotAvailableException;
import exceptions.InvalidOperationException;
import exceptions.MemberNotFoundException;
import models.Book;
import models.Member;
import models.Transaction;

import java.io.*;
import java.time.LocalDate;
import java.util.*;

/**
 * The central class tying everything together.
 * Holds all books, members, and transaction history in memory
 * (using HashMaps for O(1) lookups) and persists them to disk as CSV.
 */
public class Library {
    private Map<String, Book> books;           // key: isbn
    private Map<String, Member> members;       // key: memberId
    private List<Transaction> transactions;

    private static final String BOOKS_FILE = "data/books.csv";
    private static final String MEMBERS_FILE = "data/members.csv";
    private static final String TRANSACTIONS_FILE = "data/transactions.csv";

    public Library() {
        books = new HashMap<>();
        members = new HashMap<>();
        transactions = new ArrayList<>();
    }

    // ---------- Book management ----------

    public void addBook(Book book) throws InvalidOperationException {
        if (books.containsKey(book.getIsbn())) {
            // Same ISBN already exists -> just add more copies instead of duplicating
            books.get(book.getIsbn()).addCopies(book.getTotalCopies());
        } else {
            books.put(book.getIsbn(), book);
        }
    }

    public void removeBook(String isbn) throws InvalidOperationException {
        Book book = books.get(isbn);
        if (book == null) {
            throw new InvalidOperationException("No book found with ISBN " + isbn);
        }
        if (book.getAvailableCopies() < book.getTotalCopies()) {
            throw new InvalidOperationException("Cannot remove book: some copies are still on loan.");
        }
        books.remove(isbn);
    }

    public List<Book> searchByTitle(String keyword) {
        List<Book> results = new ArrayList<>();
        for (Book b : books.values()) {
            if (b.getTitle().toLowerCase().contains(keyword.toLowerCase())) {
                results.add(b);
            }
        }
        return results;
    }

    public List<Book> searchByAuthor(String keyword) {
        List<Book> results = new ArrayList<>();
        for (Book b : books.values()) {
            if (b.getAuthor().toLowerCase().contains(keyword.toLowerCase())) {
                results.add(b);
            }
        }
        return results;
    }

    public Collection<Book> getAllBooks() {
        return books.values();
    }

    // ---------- Member management ----------

    public void registerMember(Member member) throws InvalidOperationException {
        if (members.containsKey(member.getMemberId())) {
            throw new InvalidOperationException("Member ID " + member.getMemberId() + " already exists.");
        }
        members.put(member.getMemberId(), member);
    }

    public Member getMember(String memberId) throws MemberNotFoundException {
        Member m = members.get(memberId);
        if (m == null) {
            throw new MemberNotFoundException("No member found with ID " + memberId);
        }
        return m;
    }

    public Collection<Member> getAllMembers() {
        return members.values();
    }

    // ---------- Borrowing / returning ----------

    public Transaction borrowBook(String memberId, String isbn)
            throws MemberNotFoundException, BookNotAvailableException, InvalidOperationException {

        Member member = getMember(memberId);
        Book book = books.get(isbn);

        if (book == null || !book.isAvailable()) {
            throw new BookNotAvailableException("Book with ISBN " + isbn + " is not available right now.");
        }
        if (member.getBorrowedIsbns().size() >= member.getMaxBooksAllowed()) {
            throw new InvalidOperationException(
                    member.getName() + " has reached the max borrow limit (" + member.getMaxBooksAllowed() + ").");
        }
        if (member.getBorrowedIsbns().contains(isbn)) {
            throw new InvalidOperationException(member.getName() + " has already borrowed this book.");
        }

        LocalDate today = LocalDate.now();
        LocalDate dueDate = today.plusDays(member.getLoanPeriodDays());

        book.borrowCopy();
        member.addBorrowedBook(isbn);
        Transaction t = new Transaction(isbn, memberId, today, dueDate);
        transactions.add(t);
        return t;
    }

    public double returnBook(String memberId, String isbn)
            throws MemberNotFoundException, InvalidOperationException {

        Member member = getMember(memberId);
        if (!member.getBorrowedIsbns().contains(isbn)) {
            throw new InvalidOperationException(member.getName() + " does not currently have this book borrowed.");
        }

        Transaction activeTransaction = null;
        for (Transaction t : transactions) {
            if (t.getIsbn().equals(isbn) && t.getMemberId().equals(memberId) && !t.isReturned()) {
                activeTransaction = t;
                break;
            }
        }

        LocalDate today = LocalDate.now();
        double fine = 0.0;

        if (activeTransaction != null) {
            activeTransaction.markReturned(today);
            long overdueDays = activeTransaction.getOverdueDays(today);
            fine = overdueDays * member.getFinePerDay();
        }

        Book book = books.get(isbn);
        if (book != null) {
            book.returnCopy();
        }
        member.removeBorrowedBook(isbn);

        return fine; // 0 if returned on time
    }

    public List<Transaction> getOverdueTransactions() {
        List<Transaction> overdue = new ArrayList<>();
        LocalDate today = LocalDate.now();
        for (Transaction t : transactions) {
            if (!t.isReturned() && t.getDueDate().isBefore(today)) {
                overdue.add(t);
            }
        }
        return overdue;
    }

    public List<Transaction> getAllTransactions() {
        return transactions;
    }

    // ---------- Persistence (file I/O) ----------

    public void saveToDisk() {
        try {
            new File("data").mkdirs();

            try (PrintWriter pw = new PrintWriter(new FileWriter(BOOKS_FILE))) {
                for (Book b : books.values()) pw.println(b.toDataString());
            }
            try (PrintWriter pw = new PrintWriter(new FileWriter(MEMBERS_FILE))) {
                for (Member m : members.values()) pw.println(m.toDataString());
            }
            try (PrintWriter pw = new PrintWriter(new FileWriter(TRANSACTIONS_FILE))) {
                for (Transaction t : transactions) pw.println(t.toDataString());
            }
        } catch (IOException e) {
            System.out.println("Warning: could not save data - " + e.getMessage());
        }
    }

    public void loadFromDisk() {
        loadBooks();
        loadMembers();
        loadTransactions();
    }

    private void loadBooks() {
        File f = new File(BOOKS_FILE);
        if (!f.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.isBlank()) continue;
                Book b = Book.fromDataString(line);
                books.put(b.getIsbn(), b);
            }
        } catch (IOException e) {
            System.out.println("Warning: could not load books - " + e.getMessage());
        }
    }

    private void loadMembers() {
        File f = new File(MEMBERS_FILE);
        if (!f.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.isBlank()) continue;
                String[] parts = line.split(",", -1);
                String memberId = parts[0], name = parts[1], email = parts[2], type = parts[3];
                Member m = type.equals("Faculty")
                        ? new models.FacultyMember(memberId, name, email)
                        : new models.StudentMember(memberId, name, email);
                if (parts.length > 4 && !parts[4].isEmpty()) {
                    for (String isbn : parts[4].split(";")) {
                        m.addBorrowedBook(isbn);
                    }
                }
                members.put(memberId, m);
            }
        } catch (IOException e) {
            System.out.println("Warning: could not load members - " + e.getMessage());
        }
    }

    private void loadTransactions() {
        File f = new File(TRANSACTIONS_FILE);
        if (!f.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.isBlank()) continue;
                transactions.add(Transaction.fromDataString(line));
            }
        } catch (IOException e) {
            System.out.println("Warning: could not load transactions - " + e.getMessage());
        }
    }
}
