package red.medusa.logme.format;

import red.medusa.logme.LogMe;
import red.medusa.logme.color.ConsoleStr;
import red.medusa.logme.color.Emoji;
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
public class DefaultLogFormat implements LogFormat {

    public static ConsoleStr.RGB CYAN = new ConsoleStr.RGB(0, 135, 135);
    private PrintStream printStream;

    /**
     * 处理附加的序号
     */
    private final Function<LogThreadHolder, Object> withOrderFunction = new Function<LogThreadHolder, Object>() {
        @Override
        public Object apply(LogThreadHolder logThreadHolder) {
            return new ConsoleStr(logThreadHolder.id + "> ").color(CYAN).toString() + logThreadHolder.getLog();
        }
    };

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
        int lastIndex = trace.lastIndexOf('.');
        int secondIndex = trace.lastIndexOf('.', lastIndex - 1);
        int thirdIndex = trace.lastIndexOf('.', secondIndex - 1);
        trace = trace.substring(thirdIndex + 1);

        ConsoleStr lineLog = new ConsoleStr(new SimpleDateFormat("mm分ss秒.S").format(new Date()))  // 时间
                .color(CYAN)
                .another(" <" + Thread.currentThread().getName() + "> ")    // 线程信息
                .yellow();

        if (params.length > 0 && params[0])
            lineLog = lineLog.another(new Emoji() + " ").bold().color(subject.getColor());

        // 日志摘要
        lineLog = lineLog.another(msg.toString())
                .color(subject.getColor())
                .underline()
                .bold()
                .italics();

        if (params.length > 1 && params[1])
            lineLog = lineLog.another(" " + new Emoji())
                    .bold()
                    .color(subject.getColor());

        // 调用位置
        lineLog = lineLog.another(" | " + trace).blue();

        return new LogThreadHolder(lineLog, Thread.currentThread(), logMe.getSubjectId(),withOrderFunction);
    }

    /**
     * 输出日志之前先输出标题 Subject
     */
    @Override
    public void printSubjectLog(Subject subject,  Thread thread) {
        if (thread == null || subject.getThread() == thread) {
            ConsoleStr str = new ConsoleStr("           (" + subject.getColor() + ")     < " + subject + " >           ").color(CYAN);
            printStream.println(new ConsoleStr().append(String.join("",
                    Collections.nCopies(subject.getIndent(), "   "))).toString()
                    + str.italics().framed());
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
        printStream.println(new ConsoleStr().append(String.join("",
                Collections.nCopies(intent, "   "))).toString()+
                new ConsoleStr("\\  ").color(CYAN) + logable);
    }

    @Override
    public void stackOver(int indent) {
        printStream.println(String.join("", Collections.nCopies(indent, "	")) +
                new ConsoleStr("......").color(CYAN));
    }

    public DefaultLogFormat(PrintStream printStream) {
        this.printStream = printStream;
    }
}



















