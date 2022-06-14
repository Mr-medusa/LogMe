package red.medusa.logme.logable;

import java.util.function.Function;

/**
 * @author Mr.Medusa
 * @date 2022/6/10
 */
public class LogThreadHolder implements Logable {
    public final int id;
    private int indent;
    private final Object log;
    private final Thread thread;
    private Function<LogThreadHolder,Object> withOrderFunction;

    public LogThreadHolder(Object log, Thread thread,int id) {
       this(log,thread,id,null);
    }
    public LogThreadHolder(Object log, Thread thread,int id,Function<LogThreadHolder,Object> withOrderFunction) {
        this.log = log;
        this.thread = thread;
        this.withOrderFunction = withOrderFunction;
        this.id = id;
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

    public int getIndent() {
        return indent;
    }

    public void setIndent(int indent) {
        this.indent = indent;
    }
}
