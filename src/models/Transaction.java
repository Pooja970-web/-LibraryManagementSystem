package models;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Records a single borrow transaction: who borrowed what, when,
 * and when it's due. Also tracks the return date once returned.
 */
public class Transaction {
    private static final DateTimeFormatter FMT = DateTimeFormatter.ISO_LOCAL_DATE;

    private String isbn;
    private String memberId;
    private LocalDate borrowDate;
    private LocalDate dueDate;
    private LocalDate returnDate; // null until returned

    public Transaction(String isbn, String memberId, LocalDate borrowDate, LocalDate dueDate) {
        this.isbn = isbn;
        this.memberId = memberId;
        this.borrowDate = borrowDate;
        this.dueDate = dueDate;
        this.returnDate = null;
    }

    public String getIsbn() { return isbn; }
    public String getMemberId() { return memberId; }
    public LocalDate getBorrowDate() { return borrowDate; }
    public LocalDate getDueDate() { return dueDate; }
    public LocalDate getReturnDate() { return returnDate; }
    public boolean isReturned() { return returnDate != null; }

    public void markReturned(LocalDate date) {
        this.returnDate = date;
    }

    public long getOverdueDays(LocalDate onDate) {
        LocalDate compareDate = isReturned() ? returnDate : onDate;
        long days = java.time.temporal.ChronoUnit.DAYS.between(dueDate, compareDate);
        return Math.max(0, days);
    }

    @Override
    public String toString() {
        String status = isReturned() ? "Returned on " + returnDate : "Due " + dueDate;
        return String.format("ISBN: %s | Member: %s | Borrowed: %s | %s", isbn, memberId, borrowDate, status);
    }

    public String toDataString() {
        return isbn + "," + memberId + "," + borrowDate.format(FMT) + "," + dueDate.format(FMT) + ","
                + (returnDate == null ? "null" : returnDate.format(FMT));
    }

    public static Transaction fromDataString(String line) {
        String[] parts = line.split(",");
        Transaction t = new Transaction(parts[0], parts[1], LocalDate.parse(parts[2], FMT), LocalDate.parse(parts[3], FMT));
        if (!parts[4].equals("null")) {
            t.returnDate = LocalDate.parse(parts[4], FMT);
        }
        return t;
    }
}
