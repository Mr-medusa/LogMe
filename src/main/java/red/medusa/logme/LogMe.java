package red.medusa.logme;

import red.medusa.logme.color.ConsoleStr;
import red.medusa.logme.format.LogFormat;
import red.medusa.logme.logable.*;
import red.medusa.logme.logable.message.ParamMsg;

import java.util.*;

/**
 * @author Mr.Medusa
 * @date 2022/6/6
 */
public class LogMe extends SubjectFactory {
    private int subjectId = 1;
    private LogFormat logFormat = LogFormat.DEFAULT_LOG_FORMAT;
    private final LinkedList<ConsoleStr> allLines = new LinkedList<>();
    private final Subject root;

    public LogMe(Subject root) {
        super();
        this.root = root;
    }

    /**
     * @param msg
     * @param params 0:Emoji 1: Emoji
     * @return
     */
    public synchronized LogLine i(Object msg, boolean... params) {
        Subject subject;
        LogLine logLine = this.getLogLine();
        if (logLine == null) {
            subject = LOG_ME_DEFAULT_SUBJECT_MAP.get(this);
            if (subject == null) {
                throw new IllegalArgumentException("Subject was initiated");
            }
        } else {
            subject = logLine.getSubject();
        }
        return i(subject, msg, 2, null, params);
    }

    public synchronized LogLine i(Object msg, Integer intent, boolean... params) {
        Subject subject;
        LogLine logLine = this.getLogLine();
        if (logLine == null) {
            subject = LOG_ME_DEFAULT_SUBJECT_MAP.get(this);
            if (subject == null) {
                throw new IllegalArgumentException("Subject was initiated");
            }
        } else {
            subject = logLine.getSubject();
        }
        return i(subject, msg, intent, null, params);
    }

    public synchronized LogLine i(Subject subject, Object msg, boolean... params) {
        return i(subject, msg, 2, null, params);
    }

    /**
     * @see LogLine#prepareChildren(String...)
     */
    public synchronized LogLine i(Subject subject, Object msg, int deep, Integer indent, boolean... params) {
        if (subject == null) {
            throw new IllegalArgumentException("Subject must not be null");
        }
        if (this.getClose() != null && this.getClose()) {
            return new LogLine(null, null);
        }
        LogThreadHolder o = create(subject, msg, new Throwable().getStackTrace(), deep, params);
        if (indent != null) {
            // 因为先执行的 childParameterI,所以这里获取到的 intent 需要减1获取到正确的 intent
            o.setIndent(indent - 1);
        } else {
            o.setIndent(subject.getIndent());
        }

        if (subject.getColor() == null) {
            throw new IllegalArgumentException("Subject 已经存在: " + subject.getName());
        }
        subject.addLog(o);
        LogLine logLine = new LogLine(subject, this);
        LOG_ME_FOR_LAST_LOGLINE_MAP.put(this, logLine);
        return logLine;
    }

    public synchronized LogLine childI(String msg, boolean... params) {
        // 添加到子级
        return i(this.getLogLine().getSubject(), msg, 2, null, params);
    }


    public synchronized LogLine childParameterI(ParamMsg paramMsg, boolean... params) {
        Integer indent = null;
        if (this.containsParameter(paramMsg.getParam())) {
            indent = this.getParameterLogLine(paramMsg.getParam()).getSubject().getIndent();
        }
        paramMsg.setLogMe(this);
        return i(this.getLogLine().getSubject(), paramMsg, 2, indent, params);
    }

    public void print() {
        doPrint(null, null);
    }

    public void print(Subject subject) {
        doPrint(subject, null);
    }

    public void print(Thread thread) {
        doPrint(null, thread);
    }

    public void print(Subject subject, Thread thread) {
        doPrint(subject, thread);
    }

    public void doPrint(Subject subject, Thread thread) {
        if ((this.getClose() != null && this.getClose())) {
            return;
        }
        if (subject == null) {
            // this.logFormat.printSubjectLog(root, thread);
            for (Subject child : root.getChildren()) {
                printSubject(child, thread);
            }
        } else {
            printSubject(subject, thread);
        }
    }

    /**
     * @see LogMe#childParameterI
     * <p>
     * 清除程序某一执行块中的参数信息
     */
    public void withParamContext(Runnable runnable) {
        this.clearParameter();
        runnable.run();
        this.clearParameter();
    }

    public void printAll() {
        for (ConsoleStr consoleStr : allLines) {
            System.out.println(consoleStr);
        }
    }

    private void printSubject(Subject subject, Thread thread) {
        if (subject.getIndent() >= this.getMaxStackVal()) {
            this.logFormat.stackOver(subject.getIndent());
            return;
        }
        this.logFormat.printSubjectLog(subject, thread);
        List<Logable> lines = subject.getLogLines();
        lines.forEach(it -> {
            if (it instanceof LogThreadHolder) {
                if (thread == null || ((LogThreadHolder) it).getThread() == thread) {
                    this.logFormat.printSubject(((LogThreadHolder) it).getIndent(), it);
                }
            } else {
                printSubject(((LogLine) it).getSubject(), thread);
            }
        });
        if (!subject.getChildren().isEmpty()) {
            for (Subject child : subject.getChildren()) {
                printSubject(child, thread);
            }
        }
    }

    private LogThreadHolder create(Subject subject, Object msg, StackTraceElement[] stackTrace, int deep, boolean... params) {
        String trace = stackTrace[deep].toString();
        return this.logFormat.format(trace, subject, params, msg, this);
    }

    public Subject getRoot() {
        return root;
    }

    public void setChild(LogMe child) {
        this.root.getChildren().add(child.root);
    }

    /*
     *  color
     */
    public static ConsoleStr.RGB randomColor() {
        return Logable.colors.get(random.nextInt(Logable.colors.size()));
    }

    private static final Random random = new Random();

    // --- format
    public LogMe setLogFormat(LogFormat logFormat) {
        if (logFormat == null) {
            throw new IllegalArgumentException("logFormat must not be null");
        }
        this.logFormat = logFormat;
        return this;
    }

    public int getSubjectId() {
        return this.subjectId++;
    }

    // --- extensions
    public synchronized LogLine mPretty(String msg, Object... args) {
        return this.i(LogUtils.mPretty(msg, args), 3);
    }

    public synchronized LogLine m(String msg, Object... args) {
        return this.i(LogUtils.m(msg, args), 3);
    }

    public synchronized LogLine mArgArg(String msg, Object[] argAgs, Object... params) {
        return this.i(LogUtils.mArgArg(msg, argAgs, params), 3);
    }

    public synchronized LogLine mArgArgPretty(String msg, Object[] argAgs, Object... params) {
        return this.i(LogUtils.mArgArgPretty(msg, argAgs, params), 3);
    }

    // --- enhanced
    public synchronized LogLine i2(Object msg, Object... args) {
        if (args == null || args.length == 0) {
            return this.i(msg, 3);
        }
        return this.i(interpolation(msg.toString(),args), 3);
    }

    public static String interpolation(String msg, Object... args){
        StringBuilder sb = new StringBuilder();
        int[] segmentIndex = new int[args.length];
        int index = -1;
        int count = 0;
        while ((index = msg.indexOf("{}", index + 1)) != -1) {
            segmentIndex[count++] = index;
        }
        for (int i = 0; i < segmentIndex.length; i++) {
            if (i == 0) {
                sb.append(msg, 0, segmentIndex[i]);
            } else {
                sb.append(msg, segmentIndex[i - 1] + 2, segmentIndex[i]);
            }
            sb.append(args[i]);
        }

        if (segmentIndex[segmentIndex.length - 1] <= msg.length() - 2) {
            sb.append(msg, segmentIndex[segmentIndex.length - 1] + 2, msg.length());
        } else {
            sb.append(args[args.length - 1]);
        }
        return sb.toString();
    }

    public synchronized LogLine i3(String lineSubjectName, Object msg) {
        StackTraceElement stackTraceElement = new Throwable().getStackTrace()[1];
        String trace = stackTraceElement.getFileName()+stackTraceElement.getClassName()+stackTraceElement.getMethodName();
        Map<String, Subject> cacheSubjectMap = LOG_ME_FOR_SUBJECT_MAP.get(this);
        Subject subject;
        String key = lineSubjectName + trace;
        if (cacheSubjectMap.containsKey(key)) {
            subject = cacheSubjectMap.get(key);
        } else {
            LogLine logLine = LOG_ME_FOR_LAST_LOGLINE_MAP.get(this);

            subject = new Subject(lineSubjectName);
            subject.setLogMe(this);
            subject.setIndent(logLine.getParentSubject().getIndent() + 2);

            cacheSubjectMap.put(key,subject);

            logLine.setSubject(subject);
            // 加入到打印队列
            logLine.getParentSubject().getLogLines().add(logLine);
        }
        return this.i(subject, msg, 2, subject.getIndent());
    }
}









