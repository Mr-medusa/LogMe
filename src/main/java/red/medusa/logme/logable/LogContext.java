package red.medusa.logme.logable;

import red.medusa.logme.LogMe;

import java.util.*;

/**
 * @author Mr.Medusa
 * @date 2022/6/10
 */
public class LogContext {
    private static final ThreadLocal<LogLine> logLineThreadLocal = new ThreadLocal<>();
    private static final ThreadLocal<Map<Object, LogLine>> parameterLogLineThreadLocal = ThreadLocal.withInitial(LinkedHashMap::new);
    private static final ThreadLocal<Subject> subjectThreadLocal = new ThreadLocal<>();
    private static final ThreadLocal<LogMe> logMeThreadLocal = new ThreadLocal<>();


    public static void setSubject(Subject subject) {
        subjectThreadLocal.set(subject);
    }

    public static Subject getSubject() {
        return subjectThreadLocal.get();
    }

    public static void setLogMe(LogMe logMe) {
        logMeThreadLocal.set(logMe);
    }

    public static LogMe getLogMe() {
        return logMeThreadLocal.get();
    }


    public static void setLogLine(LogLine logLine) {
        logLineThreadLocal.set(logLine);
    }

    public static LogLine getLogLine() {
        return logLineThreadLocal.get();
    }


    public static void setParameterLogLine(Object key, LogLine logLine) {
        setLogLine(logLine);
        parameterLogLineThreadLocal.get().put(key, logLine);
    }

    public static LogLine getParameterLogLine(Object key) {
        LogLine logLine = parameterLogLineThreadLocal.get().get(key);
//        return logLine;
        return logLine != null ?  logLine :getLogLine();
    }
}
