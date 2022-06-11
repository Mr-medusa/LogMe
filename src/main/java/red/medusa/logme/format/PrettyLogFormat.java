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
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * @author Mr.Medusa
 * @date 2022/6/11
 */
public class PrettyLogFormat implements LogFormat {
    public static ConsoleStr.RGB CYAN = new ConsoleStr.RGB(0, 135, 135);
    public Map<Integer, ConsoleStr.RGB> intentToColor = new HashMap<>();
    private final PrintStream printStream;
    private static final String INTENT = "    ";
    private final String idPrefix = "";
    /**
     * 处理附加的序号
     */
    private final Function<LogThreadHolder, Object> withOrderFunction = logThreadHolder ->
            new ConsoleStr(idPrefix + logThreadHolder.id + "> ").color(CYAN).toString() +
                    logThreadHolder.getLog();

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

        ConsoleStr lineLog = new ConsoleStr(new SimpleDateFormat("mm分ss秒.S").format(new Date()))  // 时间
                .color(CYAN)
                .another(" <" + Thread.currentThread().getName() + "> ")    // 线程信息
                .yellow();

        if (params.length > 0 && params[0])
            lineLog = lineLog.another(new Emoji() + " ").bold().color(subject.getColor());

        // 日志摘要
        lineLog = lineLog.another(msg)
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

        return new LogThreadHolder(lineLog, Thread.currentThread(), withOrderFunction);
    }

    /**
     * 输出日志之前先输出标题 Subject
     */
    @Override
    public void printSubjectLog(Subject subject, int intent, Thread thread) {
        if (thread == null || subject.getThread() == thread) {
            ConsoleStr str = new ConsoleStr("           (" + subject.getColor() + ")" +
                    "     < " + subject + " >           ").color(CYAN);
            printStream.println(
                    new ConsoleStr().color(CYAN)
                            .append(nCopies(intent, "----",  "--->")).toString()
                            + idPrefix
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
        printStream.println(new ConsoleStr().color(CYAN)
                .append(
                        nCopies(intent, "|   ",null) // 子节点对齐线
                ).toString() +
                logable);
    }

    @Override
    public void stackOver(int indent) {
        printStream.println(String.join("", Collections.nCopies(indent, INTENT)) +
                new ConsoleStr("......").color(CYAN));
    }

    public PrettyLogFormat(PrintStream printStream) {
        this.printStream = printStream;
    }

    private ConsoleStr.RGB getColorByIntent(int i) {
        return intentToColor.computeIfAbsent(i, it -> LogMe.randomColor());
    }

    private String nCopies(int intent, String str, String lastHandle) {
        if (intent == 0) {
            // 处理不缩进的 Subject
            return new ConsoleStr("|").color(getColorByIntent(1)).toString();
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i <= intent; i++) {
            String strToUse = str;
            if (lastHandle != null && i == intent ) {
                strToUse = lastHandle;
            }
            sb.append(new ConsoleStr(strToUse).color(getColorByIntent(i)));
        }
        return sb.toString();
    }
}



















