package Model;

/*This is model class for the attendance tree in firebase situated under
 * "Attendance/teacherkey/randomkey"
 * intends to store extra information about the tree like subject name etc.
 *
 * */

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class AttendanceReportTree {
    private String subjectName, dateTaken, classStrength;

    public AttendanceReportTree(String subjectName, String dateTaken, String classStrength) {
        this.subjectName = subjectName;
        this.dateTaken = dateTaken;
        this.classStrength = classStrength;
    }

    public AttendanceReportTree() {
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public String getDateTaken() {
        return dateTaken;
    }

    public void setDateTaken(String dateTaken) {
        this.dateTaken = dateTaken;
    }

    public String getClassStrength() {
        return classStrength;
    }

    public void setClassStrength(String classStrength) {
        this.classStrength = classStrength;
    }
}
