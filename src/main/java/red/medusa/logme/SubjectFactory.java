package red.medusa.logme;

import red.medusa.logme.logable.Subject;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author Mr.Medusa
 * @date 2022/6/11
 */
public abstract class SubjectFactory extends Configuration{
    public static final Map<LogMe,Set<String>> LOG_ME_FOR_SUBJECT_NAMES = new HashMap<>();
    private final LogMe logMe;
    public SubjectFactory() {
        this.logMe = (LogMe) this;
        LOG_ME_FOR_SUBJECT_NAMES.put(logMe,new HashSet<>());
    }

    public Subject newSubject(Object name, Subject... children) {
        Subject subject = newSubject(name);
        Collections.addAll(subject.getChildren(), children);
        return subject;
    }
    public  Subject newSubject(Object name, String date, Subject... children) {
        Subject subject = newSubject(name, date);
        Collections.addAll(subject.getChildren(), children);
        return subject;
    }

    public  Subject newSubject(Object name) {
        return newSubject(name,new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
    }

    public  Subject newSubject(Object name, String date) {
        if (!LOG_ME_FOR_SUBJECT_NAMES.get(this.logMe).add(name.toString())) {
            throw new IllegalArgumentException("Subject name has already exists: "+name);
        }
        Subject subject = new Subject(name.toString(), date);
        this.logMe.getRoot().getChildren().add(subject);
        subject.setLogMe(this.logMe);
        return subject;
    }

    // +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-
    // +                            singleton                                     -+-
    // +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-
    public static LogMe getLogMe(String name) {
        return new LogMe(new Subject(name));
    }

    public static LogMe getLogMe(Class<?> clazz) {
        return new LogMe(new Subject(clazz.getName()));
    }
}
