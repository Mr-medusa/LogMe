package red.medusa.logme.logable;

import red.medusa.logme.color.C;
import red.medusa.logme.color.ConsoleStr;

import java.util.Arrays;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 构造器函数日志信息便捷获取方式
 *
 * @author Mr.Medusa
 * @date 2022/10/25
 */
public class LogUtils {

    public static final String PATTERN_STEP1 = "([\\.]{3}\\s*(?=\\w))|(?<=<.{1,100}>)\\s+(?=\\w)";
    public static final String PATTERN_STEP2 = "\\s+(?=\\w)";
    public static final Function<String[], ConsoleStr[]> colour = kv ->
    {
        ConsoleStr v = kv[1] != null ? C.brightBlue(kv[1]) : null;
        return new ConsoleStr[]{C.purple(prettyAnnotationForKey(kv[0])), v};
    };

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
            String[] typeVarArr = typeVar.split(PATTERN_STEP1);
            if (typeVarArr.length == 1) {
                typeVarArr = typeVarArr[0].split(PATTERN_STEP2);
            }
            if (typeVarArr.length == 3) {
                String typeVarKey = colour != null ? colour.apply(new String[]{typeVarArr[0], null})[0].toString() : typeVarArr[0];
                typeVarArr = new String[]{typeVarKey + " " + typeVarArr[1], typeVarArr[2]};
            } else if (typeVarArr.length > 3) {
                throw new IllegalArgumentException("暂不支持这样的参数：" + Arrays.toString(typeVarArr));
            }
            if (argArgs != null && typeVar.contains("...")) {
                String r = Arrays.toString((Object[]) argArgs);
                ConsoleStr[] kv = null;
                if (colour != null) {
                    kv = colour.apply(new String[]{String.join("...", typeVarArr), r});
                }
                sb.append(kv != null ? kv[0] : String.join("...", typeVarArr))
                        .append(":")
                        .append(kv != null ? kv[1] : r);
            } else {
                Object param = params == null ? null : params[i];
                if (params != null && param.getClass().isArray()) {
                    param = Arrays.toString((Object[]) param);
                }
                ConsoleStr[] kv = null;
                if (colour != null) {
                    kv = colour.apply(new String[]{String.join(" ", typeVarArr), String.valueOf(param)});
                }
                sb.append(kv != null ? kv[0] : String.join(" ", typeVarArr))
                        .append(":")
                        .append(kv != null ? kv[1] : param);
            }
            if (i < split.length - 1) {
                sb.append(",");
            }
        }
        sb.append(constructorInfo, end, constructorInfo.length());

        if (sb.indexOf("new") != -1 && sb.indexOf("new") == 0) {
            sb.replace(0, 3, C.text("new").color(new ConsoleStr.RGB(230, 115, 0)).toString());
        }
        return sb;
    }

    public static Object m(String constructorInfo, Object... params) {
        return m(constructorInfo, null, null, params);
    }

    public static Object mArgArg(String constructorInfo, Object argArgs, Object... params) {
        return m(constructorInfo, argArgs, null, params);
    }

    public static Object mPretty(String constructorInfo, Object... params) {
        return m(constructorInfo, null, colour, params);
    }

    public static Object mArgArgPretty(String constructorInfo, Object argArgs, Object... params) {
        return m(constructorInfo, argArgs, colour, params);
    }

    private static String prettyAnnotationForKey(String strKey) {
        Pattern compile = Pattern.compile("(@\\w+)");
        Matcher matcher = compile.matcher(strKey);
        while (matcher.find()) {
            String group = matcher.group(1);
            strKey = strKey.replace(group, C.green(group).toString());
        }
        String[] split = strKey.split("\\s+");
        if (split.length >= 2) {
            StringBuilder annotationStr = new StringBuilder();
            ConsoleStr typeVar = new ConsoleStr();
            for (int i = 0; i < split.length - 1; i++) {
                if(!split[i].startsWith("\033[")){
                    typeVar = typeVar.another(split[i]+" ").purple().bold();
                }else{
                    annotationStr.append(split[i]).append(" ");
                }
            }
            return annotationStr + typeVar.toString() + split[split.length - 1];
        }
        return strKey;
    }

    public static void main(String[] args) {
        System.out.println(m("main(@Null String[] args)", "hello", "world"));
        System.out.println(mArgArg("main(@Null String[] ...args)", new Object[]{"hello", "world"}));
        System.out.println(mArgArg("main(@Null String a,String[] ...args)", new Object[]{"hello", "world"}, "AAA"));
        System.out.println(mArgArg("main(@Null @Null String a,String b,String[] ...args)", new Object[]{"hello", "world"}, "AAA", 123));
        System.out.println();
        System.out.println(mPretty("main(String[] args)", "hello", "world"));
        System.out.println(mArgArgPretty("main(@Null String[] ...args)", new Object[]{"hello", "world"}));
        System.out.println(mArgArgPretty("main(@Null @Null String a,String[] ...args)", new Object[]{"hello", "world"}, "AAA"));
        System.out.println(mArgArgPretty("main(String a,String b,String[] ...args)", new Object[]{"hello", "world"}, "AAA", 123));
        System.out.println(mPretty("new (SingleObserver<? super T> observer)", "Hello"));
        System.out.println(mPretty("new (@Null SingleObserver observer)", null));
    }
}



