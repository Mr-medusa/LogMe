package red.medusa.logme.logable;

/**
 * LogLine 主要用来封装其中某一行日志，但是这行日志可能想要依赖另一组子日志
 *
 * @author Mr.Medusa
 * @date 2022/6/10
 */
public class LogLine implements Logable {
    private Subject parentSubject;
    private Subject subject;

    public LogLine(Subject subject) {
        if (subject != null) {
            this.parentSubject = subject;
        }
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
        LogContext.setLogLine(this);
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
}
