package Model;

public class Seat {
    /*This class is created to populate the seat grid for teacher activity*/
    int roll;
    boolean isPresent;

    public Seat(int roll, boolean isPresent) {
        this.roll = roll;
        this.isPresent = isPresent;
    }

    public int getRoll() {
        return roll;
    }

    public void setRoll(int roll) {
        this.roll = roll;
    }

    public boolean isPresent() {
        return isPresent;
    }

    public void setPresent(boolean present) {
        isPresent = present;
    }
}
