package red.medusa.logme.logable;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Mr.Medusa
 * @date 2022/6/10
 */
public class LogContext {
    public LogLine logLineThreadLocal = null;
    public final Map<Object, LogLine> parameterLogLineThreadLocal = new ConcurrentHashMap<>();
    public static final ThreadLocal<Map<Object, Object>> parameterLogLineThreadLocalContext = ThreadLocal.withInitial(HashMap::new);

    public void setLogLine(LogLine logLine) {
        logLineThreadLocal = logLine;
    }

    public LogLine getLogLine() {
        return logLineThreadLocal;
    }

    public void setParameterLogLine(Object key, LogLine logLine) {
        setLogLine(logLine);
        parameterLogLineThreadLocal.put(key, logLine);
    }

    public LogLine getParameterLogLine(Object key) {
        return parameterLogLineThreadLocal.get(key);
    }

    public boolean containsParameter(Object key) {
        return parameterLogLineThreadLocal.containsKey(key);
    }

    public Map<Object, LogLine> getParameterLogLineThreadLocal() {
        return parameterLogLineThreadLocal;
    }

    public void clearParameter() {
        parameterLogLineThreadLocalContext.get().clear();
        parameterLogLineThreadLocal.clear();
    }

    public static Map<Object, Object> getParameterContextMap() {
        return parameterLogLineThreadLocalContext.get();
    }
}
