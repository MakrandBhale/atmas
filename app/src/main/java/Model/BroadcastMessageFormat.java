package Model;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class BroadcastMessageFormat {
    /*This class defines the format to be used when teacher sends request to students for submitting attendance
    * as there might be multiple messages floating around we must follow
    * a format for teacher's message
    * hence this class
    * this class object will be serialized and when received by the student can be deserialized
    * if student receives another messages then we can easily differentiate the message from teacher's messages.
    * */
    private String identifier, teacherName, subjectName, teacherKey, randomKey;

    public String getDatabaseToken(){
        return "Attendance/"+this.teacherKey+"/"+this.randomKey+"/report";
    }

    public BroadcastMessageFormat(){

    }
    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public String getTeacherKey() {
        return teacherKey;
    }

    public void setTeacherKey(String teacherKey) {
        this.teacherKey = teacherKey;
    }

    public String getRandomKey() {
        return randomKey;
    }

    public void setRandomKey(String randomKey) {
        this.randomKey = randomKey;
    }

    public BroadcastMessageFormat(String identifier, String teacherName, String subjectName, String teacherKey, String randomKey) {
        this.identifier = identifier;
        this.teacherName = teacherName;
        this.subjectName = subjectName;
        this.teacherKey = teacherKey;
        this.randomKey = randomKey;
    }
}
