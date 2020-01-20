package Constants;

import com.google.android.gms.nearby.messages.Strategy;
import com.google.android.gms.nearby.messages.SubscribeOptions;

public final class Constants {
    private static final int Ttl = 300;
    public static final String PREF_NAME = "AtmasPrefs";
    public static final String USER_STUDENT = "0";
    public static final String USER_TEACHER = "1";
    public static final String REGISTRATION_DONE = "teacherRegistration";
    public static final int TEACHER = 0;
    public static final int STUDENT = 1;
    public static final String STUDENT_INFO_KEY = "STUDENT_INFO_KEY";
    public static final String TEACHER_SUBJECT_LIST = "TeacherSubList";
    public static final String TEACHER_NAME_KEY = "TEACHER_NAME";
    public static final String USER_INFO_KEY = "USER_INFO";
    public static final String USER_TYPE = "userType";

    public static final Strategy broadcastStrategy = new Strategy.Builder()
            .setDistanceType(Strategy.DISTANCE_TYPE_EARSHOT)
            .setDiscoveryMode(Strategy.DISCOVERY_MODE_BROADCAST)
            .setTtlSeconds(Ttl)
            .build();

    public static final Strategy scanStrategy = new Strategy.Builder()
            .setDistanceType(Strategy.DISTANCE_TYPE_EARSHOT)
            .setDiscoveryMode(Strategy.DISCOVERY_MODE_SCAN)
            .setTtlSeconds(Ttl)
            .build();
    public static final SubscribeOptions broadcastSubscribeOption = (new SubscribeOptions.Builder().setStrategy(broadcastStrategy)).build();
    public static final SubscribeOptions scanSubscribeOption = (new SubscribeOptions.Builder().setStrategy(scanStrategy)).build();


}