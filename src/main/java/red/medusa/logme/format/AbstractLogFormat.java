package red.medusa.logme.format;

import red.medusa.logme.LogMe;
import red.medusa.logme.color.ConsoleStr;
import red.medusa.logme.logable.LogContext;
import red.medusa.logme.logable.LogThreadHolder;
import red.medusa.logme.logable.message.ParamMsg;

import java.io.PrintStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * @author huguanghui
 * @date 2022/6/14
 */
public abstract class AbstractLogFormat implements LogFormat {
    public final String ID_PREFIX = "";
    public static final String INTENT = "    ";
    protected PrintStream printStream;
    public ConsoleStr.RGB titleAndTimeColor = new ConsoleStr.RGB(255, 255, 255);
    public Map<Integer, ConsoleStr.RGB> intentToColor = new HashMap<>();
    protected Function<LogFormat.MsgWithTrace,Object> patternFunction = null;
    /**
     * 处理附加的序号
     */
    protected final Function<LogThreadHolder, Object> withOrderFunction = logThreadHolder ->
            new ConsoleStr(ID_PREFIX + logThreadHolder.id + "> ").color(this.getTitleAndTimeColor()).toString() +
                    logThreadHolder.getLog();

    public AbstractLogFormat(PrintStream printStream) {
        this.printStream = printStream;
    }
    public LogFormat pattern(Function<LogFormat.MsgWithTrace,Object> patternFunction){
        this.patternFunction = patternFunction;
        return this;
    }

    /**
     * 调用栈信息
     */
    public String simpleTrace(String trace) {
        int lastIndex = trace.lastIndexOf('.');
        int secondIndex = trace.lastIndexOf('.', lastIndex - 1);
        int thirdIndex = trace.lastIndexOf('.', secondIndex - 1);
        return trace.substring(thirdIndex + 1);
    }

    public void handleMsgIfNecessary(Object msg) {
        if (msg instanceof ParamMsg) {
            this.parse((ParamMsg) msg);
        }
    }

    /**
     * @param intent     缩进距离
     * @param intentStr  缩进字符
     * @param headLine   每行的头字符
     * @param lastHandle 最后一个缩进字符
     * @return
     */
    protected String nCopies(int intent, String intentStr, String headLine, String lastHandle) {
        if (intent == 0) {
            // 处理不缩进的 Subject
            return new ConsoleStr(headLine).color(getColorByIntent(1)).toString();
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i <= intent; i++) {
            String strToUse = intentStr;
            if (lastHandle != null && i == intent) {
                strToUse = lastHandle;
            }
            sb.append(new ConsoleStr(strToUse).color(getColorByIntent(i)));
        }
        return sb.toString();
    }

    private ConsoleStr.RGB getColorByIntent(int i) {
        return intentToColor.computeIfAbsent(i, it -> LogMe.randomColor());
    }


    public ConsoleStr.RGB getTitleAndTimeColor() {
        return titleAndTimeColor;
    }

    public void setTitleAndTimeColor(ConsoleStr.RGB titleAndTimeColor) {
        this.titleAndTimeColor = titleAndTimeColor;
    }

    /**
     * 处理参数
     */
    @Override
    public void parse(ParamMsg paramMsg) {
        List<ParamMsg> canMatchedParamMsg = paramMsg.getCanMatchedParamMsg();
        StringBuilder sb = new StringBuilder();
        for (ParamMsg msg : canMatchedParamMsg) {
            if (msg.isNeedMatch()) {
                ConsoleStr.RGB rgb = (ConsoleStr.RGB) LogContext.getParameterContextMap()
                        .computeIfAbsent(msg.getParam(), (key) -> LogMe.randomColor());

                ConsoleStr consoleStr = new ConsoleStr(msg.getMsg()).color(rgb);
                if(msg.getHighlight() != null){
                    consoleStr = msg.getHighlight().apply(consoleStr);
                }

                sb.append(consoleStr);
            }else{
                sb.append(msg.getMsg());
            }
        }
        paramMsg.setParseMsg(sb);
    }

    @Override
    public void stackOver(int indent) {
        printStream.println(String.join("", Collections.nCopies(indent, INTENT)) + new ConsoleStr("......").color(this.getTitleAndTimeColor()));
    }
}
