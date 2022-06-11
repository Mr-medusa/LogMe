package red.medusa.logme.format;

import red.medusa.logme.logable.LogThreadHolder;
import red.medusa.logme.logable.Logable;
import red.medusa.logme.logable.Subject;

import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.function.Function;

/**
 * @author Mr.Medusa
 * @date 2022/6/11
 */
public class PlainLogFormat implements LogFormat {
    private final PrintStream printStream;

    public PlainLogFormat(PrintStream printStream) {
        this.printStream = printStream;
    }

    /**
     * 处理附加的序号
     */
    private final Function<LogThreadHolder, Object> withOrderFunction = logThreadHolder -> logThreadHolder.id + "> " + logThreadHolder.getLog();

    /**
     * 格式化日志
     *
     * @param trace   栈信息
     * @param subject Subject
     * @param params  额外参数
     * @param msg     具体的日志信息
     */
    @Override
    public LogThreadHolder format(String trace, Subject subject, boolean[] params, String msg) {
        int lastIndex = trace.lastIndexOf('.');
        int secondIndex = trace.lastIndexOf('.', lastIndex - 1);
        int thirdIndex = trace.lastIndexOf('.', secondIndex - 1);
        trace = trace.substring(thirdIndex + 1);

        StringBuilder sb = new StringBuilder();
        sb.append(new SimpleDateFormat("mm分ss秒.S").format(new Date()));
        sb.append(" <").append(Thread.currentThread().getName()).append("> ");
        sb.append(msg).append(" | ").append(trace);

        return new LogThreadHolder(sb, Thread.currentThread(), withOrderFunction);
    }

    /**
     * 输出日志之前先输出标题 Subject
     */
    @Override
    public void printSubjectLog(Subject subject, int intent, Thread thread) {
        if (thread == null || subject.getThread() == thread) {
            String sb = "|" + String.join("", Collections.nCopies(intent, "--")) + ">" +
                    "           (" + subject.getColor() + ")     < " + subject + " >           ";
            printStream.println(sb);
        }
    }

    /**
     * 打印日志
     *
     * @param intent  缩进
     * @param logable 具体的日志
     */
    @Override
    public void printSubject(int intent, Logable logable) {
        String sb = "|" +
                String.join("", Collections.nCopies(intent, "   ")) +
                "\\  " +
                logable;
        printStream.println(sb);
    }

    @Override
    public void stackOver(int indent) {
        printStream.println(String.join("", Collections.nCopies(indent, "	")) + "......");
    }
}



















