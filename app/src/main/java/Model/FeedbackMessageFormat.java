package Model;

public class FeedbackMessageFormat {
    String subject, roll;

    public FeedbackMessageFormat(String subject, String roll) {
        this.subject = subject;
        this.roll = roll;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getRoll() {
        return roll;
    }

    public void setRoll(String roll) {
        this.roll = roll;
    }
}
