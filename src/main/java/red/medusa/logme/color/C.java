package red.medusa.logme.color;


/**
 * ConsoleStr convenience way
 * 
 * @author huguanghui
 * @date 2022/10/25
 */
public class C {
    public static ConsoleStr text(Object text){
        return new ConsoleStr(text.toString());
    }

    // +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-
    // +                        foreground color                                  -+-
    // +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-
    // 子串颜色/背景色设置为黑色
    public static ConsoleStr black(String... text) {
        return ConsoleStr.newInstance().returnStr(ConsoleStr.FontColor.BLACK, text);
    }
    public static ConsoleStr brightBlack(String... text) {
        return ConsoleStr.newInstance().returnStr(ConsoleStr.FontColor.BRIGHT_BLACK, text);
    }
    public static ConsoleStr bgmBlack(String... text) {
        return ConsoleStr.newInstance().returnStr(ConsoleStr.FontColor.BACKGROUND_BLACK, text);
    }

    // 子串颜色/背景色设置为红色
    public static ConsoleStr red(String... text) {
        return ConsoleStr.newInstance().returnStr(ConsoleStr.FontColor.RED, text);
    }
    public static ConsoleStr brightRed(String... text) {
        return ConsoleStr.newInstance().returnStr(ConsoleStr.FontColor.BRIGHT_RED, text);
    }
    public static ConsoleStr bgmRed(String... text) {
        return ConsoleStr.newInstance().returnStr(ConsoleStr.FontColor.BACKGROUND_RED, text);
    }

    // 子串颜色/背景色设置为绿色
    public static ConsoleStr green(String... text) {
        return ConsoleStr.newInstance().returnStr(ConsoleStr.FontColor.GREEN, text);
    }
    public static ConsoleStr brightGreen(String... text) {
        return ConsoleStr.newInstance().returnStr(ConsoleStr.FontColor.BRIGHT_GREEN, text);
    }
    public static ConsoleStr bgmGreen(String... text) {
        return ConsoleStr.newInstance().returnStr(ConsoleStr.FontColor.BACKGROUND_GREEN, text);
    }

    // 子串颜色/背景色设置为黄色
    public static ConsoleStr yellow(String... text) {
        return ConsoleStr.newInstance().returnStr(ConsoleStr.FontColor.YELLOW, text);
    }
    public static ConsoleStr brightYellow(String... text) {
        return ConsoleStr.newInstance().returnStr(ConsoleStr.FontColor.BRIGHT_YELLOW, text);
    }
    public static ConsoleStr bgmYellow(String... text) {
        return ConsoleStr.newInstance().returnStr(ConsoleStr.FontColor.BACKGROUND_YELLOW, text);
    }

    // 子串颜色/背景色设置为蓝色
    public static ConsoleStr blue(String... text) {
        return ConsoleStr.newInstance().returnStr(ConsoleStr.FontColor.BLUE, text);
    }
    public static ConsoleStr brightBlue(String... text) {
        return ConsoleStr.newInstance().returnStr(ConsoleStr.FontColor.BRIGHT_BLUE, text);
    }
    public static ConsoleStr bgmBlue(String... text) {
        return ConsoleStr.newInstance().returnStr(ConsoleStr.FontColor.BACKGROUND_BLUE, text);
    }

    // 子串颜色/背景色设置为紫色
    public static ConsoleStr purple(String... text) {
        return ConsoleStr.newInstance().returnStr(ConsoleStr.FontColor.PURPLE, text);
    }
    public static ConsoleStr brightPurple(String... text) {
        return ConsoleStr.newInstance().returnStr(ConsoleStr.FontColor.BRIGHT_PURPLE, text);
    }
    public static ConsoleStr bgmPurple(String... text) {
        return ConsoleStr.newInstance().returnStr(ConsoleStr.FontColor.BACKGROUND_PURPLE, text);
    }

    // 子串颜色/背景色设置为青色
    public static ConsoleStr cyan(String... text) {
        return ConsoleStr.newInstance().returnStr(ConsoleStr.FontColor.CYAN, text);
    }
    public static ConsoleStr brightCyan(String... text) {
        return ConsoleStr.newInstance().returnStr(ConsoleStr.FontColor.BRIGHT_CYAN, text);
    }
    public static ConsoleStr bgmCyan(String... text) {
        return ConsoleStr.newInstance().returnStr(ConsoleStr.FontColor.BACKGROUND_CYAN, text);
    }

    // 子串颜色/背景色设置为白色
    public static ConsoleStr white(String... text) {
        return ConsoleStr.newInstance().returnStr(ConsoleStr.FontColor.WHITE, text);
    }
    public static ConsoleStr brightWhite(String... text) {
        return ConsoleStr.newInstance().returnStr(ConsoleStr.FontColor.BRIGHT_WHITE, text);
    }
    public static ConsoleStr bgmWhite(String... text) {
        return ConsoleStr.newInstance().returnStr(ConsoleStr.FontColor.BACKGROUND_WHITE, text);
    }

    /**
     * 子串颜色设置为指定的 RGB 颜色
     */
    public static ConsoleStr color(int R, int G, int B) {
        ConsoleStr.validateRgbRange(R,G,B);
        ConsoleStr.RGB rgb = new ConsoleStr.RGB(R, G, B);
        rgb.setFontColor(ConsoleStr.FontColor.RGB_FOREGROUND);
        ConsoleStr.newInstance().rgbCodes.remove(rgb);
        ConsoleStr.newInstance().rgbCodes.add(rgb);
        return ConsoleStr.newInstance().returnStr(null);
    }
    /**
     * 子串背景色设置为指定的 RGB 颜色
     */
    public static ConsoleStr background(int R, int G, int B) {
        ConsoleStr.validateRgbRange(R,G,B);
        ConsoleStr.RGB rgb = new ConsoleStr.RGB(R, G, B);
        rgb.setFontColor(ConsoleStr.FontColor.RGB_BACKGROUND);
        ConsoleStr.newInstance().rgbCodes.remove(rgb);
        ConsoleStr.newInstance().rgbCodes.add(rgb);
        return ConsoleStr.newInstance().returnStr(null);
    }
    /**
     * 子串颜色设置为指定的 RGB 颜色
     */
    public static  ConsoleStr color(ConsoleStr.RGB rgb) {
        rgb.setFontColor(ConsoleStr.FontColor.RGB_FOREGROUND);
        ConsoleStr.newInstance().rgbCodes.remove(rgb);
        ConsoleStr.newInstance().rgbCodes.add(rgb);
        return ConsoleStr.newInstance().returnStr(null);
    }
    /**
     * 子串背景色设置为指定的 RGB 颜色
     */
    public static ConsoleStr background(ConsoleStr.RGB rgb) {
        rgb.setFontColor(ConsoleStr.FontColor.RGB_BACKGROUND);
        ConsoleStr.newInstance().rgbCodes.remove(rgb);
        ConsoleStr.newInstance().rgbCodes.add(rgb);
        return ConsoleStr.newInstance().returnStr(null);
    }

    // +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-
    // +                            style                                         -+-
    // +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-
    /**
     * 加粗
     */
    public static ConsoleStr bold(String... text) {
        return ConsoleStr.newInstance().returnStr(ConsoleStr.FontStyle.BOLD, text);
    }
    /**
     * 斜体
     */
    public static ConsoleStr italics(String... text) {
        return ConsoleStr.newInstance().returnStr(ConsoleStr.FontStyle.ITALICS, text);
    }
    /**
     * 下划线
     */
    public static ConsoleStr underline(String... text) {
        return ConsoleStr.newInstance().returnStr(ConsoleStr.FontStyle.UNDERLINE, text);
    }
    /**
     * 双下划线
     */
    public static ConsoleStr doubleUnderline(String... text) {
        return ConsoleStr.newInstance().returnStr(ConsoleStr.FontStyle.DOUBLE_UNDERLINE, text);
    }

    /**
     * 删除线
     */
    public static ConsoleStr crossed(String... text) {
        return ConsoleStr.newInstance().returnStr(ConsoleStr.FontStyle.CROSSED, text);
    }

    /**
     * 框线
     */
    public static ConsoleStr framed(String... text) {
        return ConsoleStr.newInstance().returnStr(ConsoleStr.FontStyle.FRAMED, text);
    }
}
