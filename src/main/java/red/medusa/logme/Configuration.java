package red.medusa.logme;

import red.medusa.logme.color.ConsoleStr;

/**
 * @author Mr.Medusa
 * @date 2022/6/11
 */
public abstract class Configuration {
    public static boolean DEFAULT_IS_CLOSE = false;
    public static int DEFAULT_MAX_STACK_VAL = 100;
    public static ConsoleStr.RGB MSG_COLOR = null;

    public Boolean isClose = DEFAULT_IS_CLOSE;
    public Integer maxStackVal = DEFAULT_MAX_STACK_VAL;

    public Boolean getClose() {
        return isClose;
    }

    public void setClose(Boolean close) {
        isClose = close;
    }

    public Integer getMaxStackVal() {
        return maxStackVal;
    }

    public void setMaxStackVal(Integer maxStackVal) {
        this.maxStackVal = maxStackVal;
    }
}
