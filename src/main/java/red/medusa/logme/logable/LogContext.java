package red.medusa.logme.logable;

/**
 * @author Mr.Medusa
 * @date 2022/6/10
 */
public class LogContext {
    private static final ThreadLocal<LogLine> logLineThreadLocal = new ThreadLocal<>();
    public static Subject subject(){
        return logLineThreadLocal.get().getSubject();
    }
    public static void set(LogLine logLine){
        logLineThreadLocal.set(logLine);
    }
}
