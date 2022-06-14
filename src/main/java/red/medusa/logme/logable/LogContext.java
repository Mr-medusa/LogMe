package red.medusa.logme.logable;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Mr.Medusa
 * @date 2022/6/10
 */
public class LogContext {
    private static final ThreadLocal<LogLine> logLineThreadLocal = new ThreadLocal<>();
    private static final ThreadLocal<Map<Object, LogLine>> parameterLogLineThreadLocal = ThreadLocal.withInitial(LinkedHashMap::new);
    public static final ThreadLocal<Map<Object,Object>> parameterLogLineThreadLocalContext = ThreadLocal.withInitial(HashMap::new);
    // private static final ThreadLocal<Subject> subjectThreadLocal = new ThreadLocal<>();

    // public static void setSubject(Subject subject) {
    //     subjectThreadLocal.set(subject);
    // }
    //
    // public static Subject getSubject() {
    //     return subjectThreadLocal.get();
    // }

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
        return parameterLogLineThreadLocal.get().get(key);
    }

    public static boolean containsParameter(Object key) {
        return parameterLogLineThreadLocal.get().containsKey(key);
    }

    public static ThreadLocal<Map<Object, LogLine>> getParameterLogLineThreadLocal() {
        return parameterLogLineThreadLocal;
    }

    public static void clearParameter(){
        parameterLogLineThreadLocalContext.get().clear();
        parameterLogLineThreadLocal.get().clear();
    }

    public static Map<Object,Object> getParameterContextMap (){
        return parameterLogLineThreadLocalContext.get();
    }
}
