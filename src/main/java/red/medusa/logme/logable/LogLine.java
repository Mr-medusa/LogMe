package red.medusa.logme.logable;

import red.medusa.logme.LogMe;

/**
 * LogLine 主要用来封装其中某一行日志，但是这行日志可能想要依赖另一组子日志
 *
 * @author Mr.Medusa
 * @date 2022/6/10
 */
public class LogLine implements Logable,Cloneable {
    private LogLine parentLogLine;
    private Subject parentSubject;
    private Subject subject;
    private final LogMe logMe;
    private Object param;

    public LogLine(Subject subject, LogMe logMe) {
        this.parentSubject = subject;
        this.logMe = logMe;
    }

    /**
     * 新增一个 LogLine 到父 Subject 的 children 列表
     *
     * 到后续新增的 Log 都会属于当前 Subject 的子节点
     *
     * @param name 显示指定 Subject 名字,如果不存在则自动生成: 父Subject名字加-加自增计数
     */
    public synchronized Subject prepareChildren(String... name) {
        // 加入到父 Subject 的 children 列表
        this.parentSubject.getLogLines().add(this);
        // 新建的 LogMe 同时也有自己的 Subject
        this.subject = new Subject(name.length == 1 ? name[0] : parentSubject.getName() + "-" + Subject.NamingGenerator.generator());
        this.subject.setLogMe(this.logMe);
        // 设置缩进
        this.subject.setIndent(this.parentSubject.getIndent() + 1);
        // 重置 LogContext LogLine 为当前 LogLine
        this.parentLogLine = logMe.getLogLine();
        logMe.setLogLine(this);
        return subject;
    }

    public synchronized LogLine back(){
        if(logMe.getLogLine() == null || logMe.getLogLine().getParentLogLine() == null){
            logMe.setLogLine(null);
            return this;
        }
        LogLine parentLogLine = logMe.getLogLine().getParentLogLine();
        logMe.setLogLine(parentLogLine);
        return parentLogLine != null ? parentLogLine : this;
    }

    public synchronized LogLine back(int count){
        LogLine logLine = this;
        for (int i = 0; i < count; i++) {
            logLine = logLine.back();
        }
        return logLine;
    }

    public synchronized Subject prepareParameterChildren(Object param, String... name) {
        this.param = param;
        // 加入到父 Subject 的 children 列表
        this.parentSubject.getLogLines().add(this);
        // 新建的 LogMe 同时也有自己的 Subject
        this.subject = new Subject(name.length == 1 ? name[0] : parentSubject.getName() + "-" + Subject.NamingGenerator.generator());
        this.subject.setLogMe(this.logMe);
        // 设置缩进
        this.subject.setIndent(this.parentSubject.getIndent() + 1);
        // 重置 LogContext LogLine 为当前 LogLine
        logMe.setLogLine(this);
        if (!logMe.containsParameter(param)){
            logMe.setParameterLogLine(param, this);
        }else{
            // 重置 Subject indent
            LogLine parameterLogLine = logMe.getParameterLogLine(param);
            int indent = parameterLogLine.getSubject().getIndent();
            this.subject.setIndent(indent);
        }
        return subject;
    }

    /**
     * LogLine 也可能会存在 Subject
     */
    public Subject getSubject() {
        return subject;
    }

    public Subject getParentSubject() {
        return parentSubject;
    }

    public void setParentSubject(Subject parentSubject) {
        this.parentSubject = parentSubject;
    }

    public Object getParam() {
        return param;
    }

    public void setParam(Object param) {
        this.param = param;
    }

    public LogLine getParentLogLine() {
        return parentLogLine;
    }

    public void setParentLogLine(LogLine parentLogLine) {
        this.parentLogLine = parentLogLine;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }
}

