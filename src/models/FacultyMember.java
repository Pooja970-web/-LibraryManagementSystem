package models;

/**
 * Faculty members get more books and a longer loan period,
 * but a higher fine per day if they're late (since they should know better).
 */
public class FacultyMember extends Member {

    public FacultyMember(String memberId, String name, String email) {
        super(memberId, name, email);
    }

    @Override
    public int getMaxBooksAllowed() {
        return 8;
    }

    @Override
    public int getLoanPeriodDays() {
        return 30;
    }

    @Override
    public double getFinePerDay() {
        return 10.0;
    }

    @Override
    public String getMemberType() {
        return "Faculty";
    }
}
