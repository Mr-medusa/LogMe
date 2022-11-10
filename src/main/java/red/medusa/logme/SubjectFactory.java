package red.medusa.logme;

import red.medusa.logme.format.LogFormat;
import red.medusa.logme.logable.LogLine;
import red.medusa.logme.logable.Subject;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Mr.Medusa
 * @date 2022/6/11
 */
public abstract class SubjectFactory extends Configuration {
    public static final Map<LogMe, Set<Subject>> LOG_ME_FOR_SUBJECT_NAMES = new ConcurrentHashMap<>();
    // 按 Subject 分类 归集的子 LogLine
    public static final Map<LogMe, Map<String, Subject>> LOG_ME_FOR_SUBJECT_MAP = new ConcurrentHashMap<>();
    // 子 LogLine
    public static final Map<LogMe, LogLine> LOG_ME_FOR_LAST_LOGLINE_MAP = new ConcurrentHashMap<>();
    // 默认的 Subject
    public static final Map<LogMe, Subject> LOG_ME_DEFAULT_SUBJECT_MAP = new ConcurrentHashMap<>();

    public SubjectFactory() {
        LOG_ME_FOR_SUBJECT_NAMES.put((LogMe) this, new HashSet<>());
        LOG_ME_FOR_SUBJECT_MAP.put((LogMe) this, new HashMap<>());
        // --- parameter
        parameterLogLineThreadLocal.put((LogMe) this, new HashMap<>());
        parameterLogLineThreadLocalContext.put((LogMe) this, new HashMap<>());
    }

    public Subject newSubject(Object name, Subject... children) {
        Subject subject = newSubject(name);
        Collections.addAll(subject.getChildren(), children);
        return subject;
    }

    public Subject newSubject(Object name) {
        if (LOG_ME_FOR_SUBJECT_NAMES.get(this).contains(new Subject(name.toString()))) {
            throw new IllegalArgumentException("Subject name has already exists: " + name);
        }
        Subject subject = new Subject(name.toString());
        ((LogMe) this).getRoot().getChildren().add(subject);
        subject.setLogMe((LogMe) this);
        return subject;
    }

    public Subject findSubjectByName(Object name){
        if (LOG_ME_FOR_SUBJECT_NAMES.get(this).contains(new Subject(name.toString()))) {
            for (Subject subject : LOG_ME_FOR_SUBJECT_NAMES.get(this)) {
                if(subject.getName().equals(name)){
                    return subject;
                }
            }
        }
        return null;
    }

    // +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-
    // +                            singleton                                     -+-
    // +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-
    public static LogMe getLogMe(String name) {
        LogMe logMe = new LogMe(new Subject(name));
        Subject subject = logMe.newSubject(name);
        LOG_ME_DEFAULT_SUBJECT_MAP.put(logMe, subject);
        return logMe;
    }

    public static LogMe getLogMe(Class<?> clazz) {
        LogMe logMe = new LogMe(new Subject(clazz.getName()));
        Subject subject = logMe.newSubject(clazz.getName());
        LOG_ME_DEFAULT_SUBJECT_MAP.put(logMe, subject);
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
        LOG_ME_DEFAULT_SUBJECT_MAP.put(logMe, logMe.newSubject(subject));
        return logMe;
    }

    public LogMe createCurrentSubject(Class<?> subjectClass) {
        LogMe logMe = (LogMe) this;
        LOG_ME_DEFAULT_SUBJECT_MAP.put(logMe, logMe.newSubject(subjectClass.getName()));
        return logMe;
    }

    public LogMe toggleCurrentSubject(Subject subject) {
        LogMe logMe = (LogMe) this;
        LOG_ME_DEFAULT_SUBJECT_MAP.put(logMe, subject);
        return logMe;
    }

    // --- child
    public synchronized LogMe child(String... name) {
        LogLine logLine = LOG_ME_FOR_LAST_LOGLINE_MAP.get(this);
        if (logLine == null) {
            return null;
        }
        logLine.prepareChildren(name);
        return (LogMe) this;
    }

    public synchronized LogMe paramChild(Object param, String... name) {
        LogLine logLine = LOG_ME_FOR_LAST_LOGLINE_MAP.get(this);
        if (logLine == null) {
            return null;
        }
        logLine.prepareParameterChildren(param, name);
        return (LogMe) this;
    }

    public LogLine back() {
        return this.getLogLine().back();
    }

    public LogLine back(int n) {
        return this.getLogLine().back(n);
    }


    // +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-
    // +                             Parameter                                    -+-
    // +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-
    public static final Map<LogMe, Map<Object, LogLine>> parameterLogLineThreadLocal = new ConcurrentHashMap<>();
    public static final Map<LogMe, Map<Object, Object>> parameterLogLineThreadLocalContext = new ConcurrentHashMap<>();
    public Map<LogMe, LogLine> logLineThreadLocal = new ConcurrentHashMap<>();

    public void setLogLine(LogLine logLine) {
        if (logLine == null) {
            logLine = new LogLine(null, null);
        }
        logLineThreadLocal.put((LogMe) this, logLine);
    }

    public LogLine getLogLine() {
        LogLine logLine = logLineThreadLocal.get(this);
        if (logLine == null) {
            return null;
        }
        return logLine.getSubject() == null ? null : logLine;
    }

    public void setParameterLogLine(Object key, LogLine logLine) {
        setLogLine(logLine);
        parameterLogLineThreadLocal.get(this).put(key, logLine);
    }

    public LogLine getParameterLogLine(Object key) {
        return parameterLogLineThreadLocal.get(this).get(key);
    }

    public boolean containsParameter(Object key) {
        return parameterLogLineThreadLocal.get(this).containsKey(key);
    }

    public Map<Object, LogLine> getParameterLogLineThreadLocal() {
        return parameterLogLineThreadLocal.get(this);
    }

    public Map<Object, Object> getParameterContextMap() {
        return parameterLogLineThreadLocalContext.get(this);
    }

    public void clearParameter() {
        parameterLogLineThreadLocalContext.get(this).clear();
        parameterLogLineThreadLocal.get(this).clear();
    }
}
