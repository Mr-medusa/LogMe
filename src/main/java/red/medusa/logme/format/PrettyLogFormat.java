package red.medusa.logme.format;

import red.medusa.logme.LogMe;
import red.medusa.logme.color.ConsoleStr;
import red.medusa.logme.color.Emoji;
import red.medusa.logme.logable.LogThreadHolder;
import red.medusa.logme.logable.Logable;
import red.medusa.logme.logable.Subject;

import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Mr.Medusa
 * @date 2022/6/11
 */
public class PrettyLogFormat extends AbstractLogFormat {


    public PrettyLogFormat(PrintStream printStream) {
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
        ConsoleStr lineLog = new ConsoleStr(new SimpleDateFormat("mm分ss秒.S").format(new Date()))  // 时间
                .color(this.getTitleAndTimeColor())
                .another(" <" + Thread.currentThread().getName() + "> ")                            // 线程信息
                .yellow();

        if (params.length > 0 && params[0])
            lineLog = lineLog.another(new Emoji() + " ").bold().color(subject.getColor());

        // 日志摘要
        lineLog = lineLog.another(msg.toString())                                                   // 日志摘要
                .color(subject.getColor())
                .underline()
                .bold()
                .italics();

        if (params.length > 1 && params[1])
            lineLog = lineLog.another(" " + new Emoji())
                    .bold()
                    .color(subject.getColor());

        // 调用位置
        lineLog = lineLog.another(" | " + trace).blue();                                            // 调用位置

        return new LogThreadHolder(lineLog, Thread.currentThread(), logMe.getSubjectId(), withOrderFunction);
    }

    /**
     * 输出日志之前先输出标题 Subject
     * <p>
     * 有缩进的 Subject 标题以 | 打头 否则 ----> 打头,例如:
     * <p>
     * --->           (LimeGreen)     < Fibonacci-0 >
     */
    @Override
    public void printSubjectLog(Subject subject, Thread thread) {
        if (thread == null || subject.getThread() == thread) {
            ConsoleStr str = new ConsoleStr("           (" + subject.getColor() + ")     < " + subject + " >           ").color(this.getTitleAndTimeColor());
            printStream.println(new ConsoleStr().color(this.getTitleAndTimeColor())
                    .append(nCopies(subject.getIndent(), "----", "|", "--->")).toString() + ID_PREFIX + str.italics().framed());
        }
    }


    /**
     * 打印日志
     *
     * @param intent  缩进
     * @param logable 具体的日志
     *                <p>
     *                例如:
     *                |   |   |   |   |   6> 27分37秒.378 <main> final number is 1 | TraceTest.fibonacci(TraceTest.java:37)
     */
    @Override
    public void printSubject(int intent, Logable logable) {
        printStream.println(new ConsoleStr().color(this.getTitleAndTimeColor())
                .append(nCopies(intent, "|   ", "|", null)).toString() + logable);
    }


}



















