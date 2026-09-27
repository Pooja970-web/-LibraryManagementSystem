package models;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract base class for all library members.
 * Demonstrates inheritance: StudentMember and FacultyMember
 * extend this and override the borrowing rules.
 */
public abstract class Member {
    private String memberId;
    private String name;
    private String email;
    private List<String> borrowedIsbns; // ISBNs currently borrowed

    public Member(String memberId, String name, String email) {
        this.memberId = memberId;
        this.name = name;
        this.email = email;
        this.borrowedIsbns = new ArrayList<>();
    }

    public String getMemberId() { return memberId; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public List<String> getBorrowedIsbns() { return borrowedIsbns; }

    public void addBorrowedBook(String isbn) {
        borrowedIsbns.add(isbn);
    }

    public void removeBorrowedBook(String isbn) {
        borrowedIsbns.remove(isbn);
    }

    // Abstract methods force each subclass to define its own rules
    public abstract int getMaxBooksAllowed();
    public abstract int getLoanPeriodDays();
    public abstract double getFinePerDay();
    public abstract String getMemberType();

    @Override
    public String toString() {
        return String.format("%-8s | %-20s | %-25s | %-10s | Borrowed: %d/%d",
                memberId, name, email, getMemberType(), borrowedIsbns.size(), getMaxBooksAllowed());
    }

    public String toDataString() {
        return memberId + "," + name + "," + email + "," + getMemberType() + "," + String.join(";", borrowedIsbns);
    }
}
