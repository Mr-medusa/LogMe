package red.medusa.logme.format;

import red.medusa.logme.logable.LogThreadHolder;
import red.medusa.logme.logable.Logable;
import red.medusa.logme.logable.Subject;

/**
 * @author Mr.Medusa
 * @date 2022/6/11
 */
public interface LogFormat {

    LogFormat DEFAULT_LOG_FORMAT = new DefaultLogFormat(DefaultLogFormat.CYAN, System.out);

    LogThreadHolder format(String trace, Subject subject, boolean[] params, String msg);

    void printSubjectLog(Subject subject, int intent, Thread thread);

    void printSubject(int intent, Logable logable);

    void stackOver(int indent);
}
