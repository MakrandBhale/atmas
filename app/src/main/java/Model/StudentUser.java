package Model;

public class StudentUser extends User {
    private String roll, studentClass, grNo;

    public StudentUser(){

    }
    public StudentUser(String name, String email, String uid, String userType, String roll, String studentClass, String grNo) {
        super(name, email, uid, userType);
        this.roll = roll;
        this.studentClass = studentClass;
        this.grNo = grNo;
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
