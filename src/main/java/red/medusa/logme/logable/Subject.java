package red.medusa.logme.logable;

import red.medusa.logme.Configuration;
import red.medusa.logme.color.ConsoleStr;
import red.medusa.logme.LogMe;

import java.util.*;

/**
 * @author Mr.Medusa
 * @date 2022/6/9
 */
public class Subject {
    private final String name;
    private final String date;
    private final ConsoleStr.RGB color = Configuration.MSG_COLOR != null ? Configuration.MSG_COLOR : LogMe.randomColor();

    private final List<Logable> logLines = new LinkedList<>();
    private final List<Subject> children = new ArrayList<>();
    private final Thread thread = Thread.currentThread();

    public Subject(String name) {
        this.name = name;
        this.date = null;
    }
    public Subject(String name, String date) {
        this.name = name;
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public ConsoleStr.RGB getColor() {
        return color;
    }

    public Subject mount(Subject subject) {
        // 如果存在于 root 的子节点中则移除
        LogMe logMe = LogContext.getLogMe();
        logMe.getRoot().getChildren().remove(this);
        subject.children.add(this);
        return this;
    }

    public List<Subject> getChildren() {
        return children;
    }

    public List<Logable> getLogLines() {
        return logLines;
    }


    public void addLog(LogThreadHolder consoleStrHolder) {
        this.logLines.add(consoleStrHolder);
    }

    public Thread getThread() {
        return thread;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Subject that = (Subject) o;
        return name.equals(that.name) &&
                date.equals(that.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, date);
    }

    @Override
    public String toString() {
        return name;
    }

    public static class NamingGenerator {
        public static int counter = 0;

        public static int generator() {
            return counter++;
        }
    }


}
