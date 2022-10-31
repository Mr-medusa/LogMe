package red.medusa.logme;

import red.medusa.logme.format.LogFormat;
import red.medusa.logme.logable.LogContext;
import red.medusa.logme.logable.Subject;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Mr.Medusa
 * @date 2022/6/11
 */
public abstract class SubjectFactory extends Configuration {
    private final LogContext logContext = new LogContext();
    public static final Map<LogMe, Set<String>> LOG_ME_FOR_SUBJECT_NAMES = new ConcurrentHashMap<>();
    public static final Map<LogMe, Subject> LOGME_DEFAULT_SUBJECT_MAP = new ConcurrentHashMap<>();

    public SubjectFactory() {
        LOG_ME_FOR_SUBJECT_NAMES.put((LogMe) this, new HashSet<>());
    }

    public Subject newSubject(Object name, Subject... children) {
        Subject subject = newSubject(name);
        Collections.addAll(subject.getChildren(), children);
        return subject;
    }

    public Subject newSubject(Object name, String date, Subject... children) {
        Subject subject = newSubject(name, date);
        Collections.addAll(subject.getChildren(), children);
        return subject;
    }

    public Subject newSubject(Object name) {
        return newSubject(name, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
    }

    public Subject newSubject(Object name, String date) {
        if (!LOG_ME_FOR_SUBJECT_NAMES.get(this).add(name.toString())) {
            throw new IllegalArgumentException("Subject name has already exists: " + name);
        }
        Subject subject = new Subject(name.toString(), date);
        ((LogMe) this).getRoot().getChildren().add(subject);
        subject.setLogMe((LogMe) this);
        return subject;
    }

    // +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-
    // +                            singleton                                     -+-
    // +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-
    public static LogMe getLogMe(String name) {
        LogMe logMe = new LogMe(new Subject(name));
        Subject subject = logMe.newSubject(name);
        LOGME_DEFAULT_SUBJECT_MAP.put(logMe, subject);
        return logMe;
    }

    public static LogMe getLogMe(Class<?> clazz) {
        LogMe logMe = new LogMe(new Subject(clazz.getName()));
        Subject subject = logMe.newSubject(clazz.getName());
        LOGME_DEFAULT_SUBJECT_MAP.put(logMe, subject);
        return logMe;
    }

    public static LogMe getLogMe(String name, LogFormat logFormat) {
        return getLogMe(name).setLogFormat(logFormat);
    }

    public static LogMe getLogMe(Class<?> clazz, LogFormat logFormat) {
        return getLogMe(clazz).setLogFormat(logFormat);
    }

    public LogMe createCurrentSubject(String subject) {
        LogMe logMe = (LogMe) this;
        LOGME_DEFAULT_SUBJECT_MAP.put(logMe, logMe.newSubject(subject));
        return logMe;
    }

    public LogMe createCurrentSubject(Class<?> subjectClass) {
        LogMe logMe = (LogMe) this;
        LOGME_DEFAULT_SUBJECT_MAP.put(logMe, logMe.newSubject(subjectClass.getName()));
        return logMe;
    }

    public LogMe toggleCurrentSubject(Subject subject) {
        LogMe logMe = (LogMe) this;
        LOGME_DEFAULT_SUBJECT_MAP.put(logMe, subject);
        return logMe;
    }


    // +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-
    // +                            log context                                   -+-
    // +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-


    public LogContext getLogContext() {
        return logContext;
    }
}
