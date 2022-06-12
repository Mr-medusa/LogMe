package red.medusa.logme.logable;

import red.medusa.logme.LogMe;

/**
 * LogLine 主要用来封装其中某一行日志，但是这行日志可能想要依赖另一组子日志
 *
 * @author Mr.Medusa
 * @date 2022/6/10
 */
public class LogLine implements Logable {
    private Subject parentSubject;
    private Subject subject;
    private final LogMe logMe;
    private Object param;

    public LogLine(Subject subject,LogMe logMe) {
        if (subject != null) {
            this.parentSubject = subject;
        }
        this.logMe = logMe;
    }

    /**
     * 后续新增的 Log 都会属于当前 Subject 的子节点
     *
     * @param name 显示指定 Subject 名字,如果不存在则自动生成: 父Subject名字加-加自增计数
     */
    public synchronized Subject prepareChildren(String... name) {
        if (this.parentSubject != null) {
            this.parentSubject.getLogLines().add(this);
            subject = new Subject(name.length == 1 ? name[0] : parentSubject.getName() + "-" + Subject.NamingGenerator.generator());
        }
        this.subject.setLogMe(this.logMe);
        LogContext.setLogLine(this);
        return subject;
    }
    public synchronized Subject prepareParameterChildren(Object param,String... name) {
        if (this.parentSubject != null) {
            this.parentSubject.getLogLines().add(this);
            this.subject = new Subject(name.length == 1 ? name[0] : parentSubject.getName() + "-" + Subject.NamingGenerator.generator());
        }
        this.subject.setLogMe(this.logMe);
        this.param = param;
        if(LogContext.getParameterLogLine(param)==null)
            LogContext.setParameterLogLine(param,this);
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
}
