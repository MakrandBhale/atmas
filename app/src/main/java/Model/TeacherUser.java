package Model;
import com.makarand.atmas.TeacherRegistration;

import java.util.ArrayList;
import java.util.Map;

public class TeacherUser extends User {
    private ArrayList<Subject> subjectListArray;

    public TeacherUser(){

    }
    public TeacherUser(String name, String email, String uid, String userType, ArrayList<Subject> subjectListArray) {
        super(name, email, uid, userType);
        this.subjectListArray = subjectListArray;
    }


    public ArrayList<Subject> getSubjectListArray() {
        return subjectListArray;
    }

    public void setSubjectListArray(ArrayList<Subject> subjectListArray) {
        this.subjectListArray = subjectListArray;
    }
}
