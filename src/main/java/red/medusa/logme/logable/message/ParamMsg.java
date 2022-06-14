package red.medusa.logme.logable.message;

import red.medusa.logme.color.ConsoleStr;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author huguanghui
 * @date 2022/6/13
 */
public class ParamMsg {
    private Object param;
    private String msg;
    private boolean needMatch;
    private List<ParamMsg> canMatchedParamMsg = new ArrayList<>();
    private Object parseMsg;
    private Function<ConsoleStr,ConsoleStr> highlightFun;

    public ParamMsg append(Object param,String msg, boolean needMatch) {
        canMatchedParamMsg.add(new ParamMsg(param, msg,needMatch));
        return this;
    }

    public ParamMsg(Object param, String msg,boolean needMatch) {
        this.param = param;
        this.msg = msg;
        this.needMatch = needMatch;
        canMatchedParamMsg.add(this);
    }

    @Override
    public String toString() {
        return parseMsg != null ? parseMsg.toString() :
                canMatchedParamMsg.stream().map(ParamMsg::getMsg).collect(Collectors.joining());
    }

    public Object getParam() {
        return param;
    }

    public void setParam(Object param) {
        this.param = param;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public List<ParamMsg> getCanMatchedParamMsg() {
        return canMatchedParamMsg;
    }

    public void setCanMatchedParamMsg(List<ParamMsg> canMatchedParamMsg) {
        this.canMatchedParamMsg = canMatchedParamMsg;
    }

    public Object getParseMsg() {
        return parseMsg;
    }

    public void setParseMsg(Object parseMsg) {
        this.parseMsg = parseMsg;
    }

    public boolean isNeedMatch() {
        return needMatch;
    }

    public void setNeedMatch(boolean needMatch) {
        this.needMatch = needMatch;
    }

    public Function<ConsoleStr, ConsoleStr> getHighlight() {
        return highlightFun;
    }

    public ParamMsg highlight(Function<ConsoleStr, ConsoleStr> highlightFun) {
        this.highlightFun = highlightFun;
        return this;
    }


}




















