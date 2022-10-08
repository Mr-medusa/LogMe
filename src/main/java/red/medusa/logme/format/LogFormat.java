package red.medusa.logme.format;

import red.medusa.logme.LogMe;
import red.medusa.logme.logable.LogThreadHolder;
import red.medusa.logme.logable.Logable;
import red.medusa.logme.logable.Subject;
import red.medusa.logme.logable.message.ParamMessageParser;

/**
 * @author Mr.Medusa
 * @date 2022/6/11
 */
public interface LogFormat extends ParamMessageParser {

    LogFormat DEFAULT_LOG_FORMAT = new PrettyLogFormat(System.out);

    LogThreadHolder format(String trace, Subject subject, boolean[] params, Object msg, LogMe logMe);

    /**
     * 处理标题
     * <p>
     * 输出日志之前先输出标题 Subject
     */
    void printSubjectLog(Subject subject, Thread thread);

    /**
     * 处理缩进
     *
     * @param intent  缩进
     * @param logable 具体的日志
     */
    void printSubject(int intent, Logable logable);

    void stackOver(int indent);
}
