package models;

/**
 * Student members can borrow fewer books, for a shorter period,
 * but pay a smaller fine per day than faculty.
 */
public class StudentMember extends Member {

    public StudentMember(String memberId, String name, String email) {
        super(memberId, name, email);
    }

    @Override
    public int getMaxBooksAllowed() {
        return 3;
    }

    @Override
    public int getLoanPeriodDays() {
        return 14;
    }

    @Override
    public double getFinePerDay() {
        return 5.0; // currency units per day late
    }

    @Override
    public String getMemberType() {
        return "Student";
    }
}
