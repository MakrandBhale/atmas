package Model;

public class StudentInfo {
    private String name,roll, studentClass, grNo;
    private String subject;

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public StudentInfo(String name, String roll, String studentClass, String grNo) {
        this.name = name;
        this.roll = roll;
        this.studentClass = studentClass;
        this.grNo = grNo;
    }

    public StudentInfo(String name, String roll, String studentClass, String grNo, String subject) {
        this.name = name;
        this.roll = roll;
        this.studentClass = studentClass;
        this.grNo = grNo;
        this.subject = subject;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRoll() {
        return roll;
    }

    public void setRoll(String roll) {
        this.roll = roll;
    }

    public String getStudentClass() {
        return studentClass;
    }

    public void setStudentClass(String studentClass) {
        this.studentClass = studentClass;
    }

    public String getGrNo() {
        return grNo;
    }

    public void setGrNo(String grNo) {
        this.grNo = grNo;
    }
}
