package red.medusa.logme.logable;

import java.util.function.Function;

/**
 * @author Mr.Medusa
 * @date 2022/6/10
 */
public class LogThreadHolder implements Logable {
    private static int counter = 0;
    public final int id = counter++;
    private final Object log;
    private final Thread thread;
    private Function<LogThreadHolder,Object> withOrderFunction;

    public LogThreadHolder(Object log, Thread thread) {
        this.log = log;
        this.thread = thread;
    }
    public LogThreadHolder(Object log, Thread thread,Function<LogThreadHolder,Object> withOrderFunction) {
        this.log = log;
        this.thread = thread;
        this.withOrderFunction = withOrderFunction;
    }
    @Override
    public String toString() {
        return this.withOrderFunction!=null ? this.withOrderFunction.apply(this).toString() : this.log.toString();
    }

    public Thread getThread() {
        return thread;
    }


    public int getId() {
        return id;
    }

    public Object getLog() {
        return log;
    }
}
