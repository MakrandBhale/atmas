package Model;

public class Subject{
    private String subjectName;
    private String strength;

    public Subject(){

    }
    public Subject(String subjectName, String strength) {
        this.subjectName = subjectName;
        this.strength = strength;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public String getStrength() {
        return strength;
    }
}
