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
    // 进用来获取LogMe里的Root使用,用来简化 mount 时额外传递
    private LogMe logMe;
    private int indent = 0;
    private final String name;
    private final ConsoleStr.RGB color = Configuration.MSG_COLOR != null ? Configuration.MSG_COLOR : LogMe.randomColor();

    private final List<Logable> logLines = new LinkedList<>();
    private final List<Subject> children = new ArrayList<>();
    private final Thread thread = Thread.currentThread();

    public Subject(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public ConsoleStr.RGB getColor() {
        return color;
    }

    public Subject mount(Subject subject) {
        // 如果存在于 root 的子节点中则移除
        this.logMe.getRoot().getChildren().remove(this);
        this.setIndent(subject.getIndent()+1);
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
    public String toString() {
        return name;
    }

    public void setLogMe(LogMe logMe) {
        this.logMe = logMe;
    }

    public int getIndent() {
        return indent;
    }

    public void setIndent(int indent) {
        this.indent = indent;
    }

    public static class NamingGenerator {
        public static int counter = 0;

        public static int generator() {
            return counter++;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Subject)) return false;
        Subject subject = (Subject) o;
        return name.equals(subject.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
