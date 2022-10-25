package red.medusa.logme.logable;

import red.medusa.logme.color.C;
import red.medusa.logme.color.ConsoleStr;

import java.util.Arrays;
import java.util.function.Function;

/**
 * 构造器函数日志信息便捷获取方式
 *
 * @author Mr.Medusa
 * @date 2022/10/25
 */
public class LogUtils {

    /**
     * 构造器函数消息便捷方式
     */
    private static Object m(String constructorInfo, Object argArgs, Function<String[], ConsoleStr[]> colour, Object... params) {
        int start = constructorInfo.indexOf("(");
        int end = constructorInfo.lastIndexOf(")");
        String substring = constructorInfo.substring(start + 1, end);
        String[] split = substring.split(",");
        StringBuilder sb = new StringBuilder();
        sb.append(constructorInfo, 0, start + 1);
        for (int i = 0; i < split.length; i++) {
            String typeVar = split[i];
            String[] typeVarArr = typeVar.split("([\\.]{3}\\s*(?=\\w))|\\s+(?=\\w)");
            if (argArgs != null && typeVar.contains("...")) {
                String typeVarArgArg = "..." + typeVarArr[1];
                String r = Arrays.toString((Object[]) argArgs);
                ConsoleStr[] kv = null;
                if (colour != null) {
                    kv = colour.apply(new String[]{typeVarArgArg.trim(), r});
                }
                sb.append(kv != null ? kv[0] : typeVarArgArg.trim())
                        .append(":")
                        .append(kv != null ? kv[1] : r);
            } else {
                Object param = params[i];
                if (param.getClass().isArray()) {
                    param = Arrays.toString((Object[]) param);
                }
                ConsoleStr[] kv = null;
                if (colour != null) {
                    kv = colour.apply(new String[]{typeVarArr[1].trim(), String.valueOf(param)});
                }
                sb.append(kv != null ? kv[0] : typeVarArr[1].trim())
                        .append(":")
                        .append(kv != null ? kv[1] : param);
            }
            if (i < split.length - 1) {
                sb.append(",");
            }
        }
        sb.append(constructorInfo, end, constructorInfo.length());
        return sb;
    }

    public static Object m(String constructorInfo, Object... params) {
        return m(constructorInfo, null, null, params);
    }

    public static Object mArgArg(String constructorInfo, Object argArgs, Object... params) {
        return m(constructorInfo, argArgs, null, params);
    }

    public static Object mPretty(String constructorInfo, Object... params) {
        return m(constructorInfo, null, kv -> new ConsoleStr[]{C.purple(kv[0]).bold(), C.brightBlue(kv[1])}, params);
    }

    public static Object mArgArgPretty(String constructorInfo, Object argArgs, Object... params) {
        return m(constructorInfo, argArgs, kv -> new ConsoleStr[]{C.purple(kv[0]).bold(), C.brightBlue(kv[1])}, params);
    }
}



