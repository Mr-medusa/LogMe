package red.medusa.logme.format;

import red.medusa.logme.LogMe;
import red.medusa.logme.color.ConsoleStr;
import red.medusa.logme.logable.LogThreadHolder;
import red.medusa.logme.logable.Logable;
import red.medusa.logme.logable.Subject;

import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;

/**
 * @author Mr.Medusa
 * @date 2022/6/11
 */
public class PlainLogFormat extends AbstractLogFormat {

    public PlainLogFormat(PrintStream printStream) {
        super(printStream);
    }

    /**
     * 格式化日志
     *
     * @param trace   栈信息
     * @param subject Subject
     * @param params  额外参数
     * @param msg     具体的日志信息
     */
    @Override
    public LogThreadHolder format(String trace, Subject subject, boolean[] params, Object msg, LogMe logMe) {
        this.handleMsgIfNecessary(msg);
        trace = this.simpleTrace(trace);
        StringBuilder sb = new StringBuilder();
        sb.append(new SimpleDateFormat("mm分ss秒.S").format(new Date()));
        sb.append(" <").append(Thread.currentThread().getName()).append("> ");
        sb.append(msg).append(" | ").append(trace);
        return new LogThreadHolder(sb, Thread.currentThread(),logMe.getSubjectId(), withOrderFunction);
    }

    /**
     * 处理标题
     *
     * 输出日志之前先输出标题 Subject
     */
    @Override
    public void printSubjectLog(Subject subject,  Thread thread) {
        if (thread == null || subject.getThread() == thread) {
            String sb = "|" + String.join("", Collections.nCopies(subject.getIndent(), "   ")) +
                    new ConsoleStr( "           (" + subject.getColor() + ")     < " + subject + " >           ")
                    .underline();
            printStream.println(sb);
        }
    }

    /**
     * 处理缩进
     *
     * @param intent  缩进
     * @param logable 具体的日志
     */
    @Override
    public void printSubject(int intent, Logable logable) {
        String sb = "|" + String.join("", Collections.nCopies(intent, "   ")) + "\\ " +  logable;
        printStream.println(sb);
    }

}





















