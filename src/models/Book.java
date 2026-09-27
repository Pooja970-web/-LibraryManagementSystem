package models;

/**
 * Represents a single book in the library.
 * Each book has a unique ISBN, and tracks how many copies
 * are owned in total vs. how many are currently available.
 */
public class Book {
    private String isbn;
    private String title;
    private String author;
    private String genre;
    private int totalCopies;
    private int availableCopies;

    public Book(String isbn, String title, String author, String genre, int totalCopies) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.genre = genre;
        this.totalCopies = totalCopies;
        this.availableCopies = totalCopies;
    }

    // Getters
    public String getIsbn() { return isbn; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public String getGenre() { return genre; }
    public int getTotalCopies() { return totalCopies; }
    public int getAvailableCopies() { return availableCopies; }

    public boolean isAvailable() {
        return availableCopies > 0;
    }

    public void borrowCopy() {
        if (availableCopies > 0) {
            availableCopies--;
        }
    }

    public void returnCopy() {
        if (availableCopies < totalCopies) {
            availableCopies++;
        }
    }

    public void addCopies(int count) {
        totalCopies += count;
        availableCopies += count;
    }

    @Override
    public String toString() {
        return String.format("%-10s | %-30s | %-20s | %-15s | %d/%d available",
                isbn, title, author, genre, availableCopies, totalCopies);
    }

    // Used for saving to file (CSV-like format)
    public String toDataString() {
        return isbn + "," + title + "," + author + "," + genre + "," + totalCopies + "," + availableCopies;
    }

    public static Book fromDataString(String line) {
        String[] parts = line.split(",");
        Book book = new Book(parts[0], parts[1], parts[2], parts[3], Integer.parseInt(parts[4]));
        book.availableCopies = Integer.parseInt(parts[5]);
        return book;
    }
}
