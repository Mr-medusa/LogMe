package red.medusa.logme.color;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 为Eclipse/IDEA/CMD输出控制台的文本着色
 *
 * @author Mr.Medusa
 * @date 2022/4/30
 */
public class ConsoleStr {
	/**
	 * 控制 another 的文本是否使用当前样式，默认情况下为 false
	 */
	private boolean transitivity = true;
	/**
	 * 设置每次调用样式方法之后是否总是返回新的实例，默认情况下为 false
	 */
	private boolean alwaysReturnNewOne = false;
	// --- 常量
	private static final String M = "m";
	private static final String WRAPPER = "\033[";
	private static final String $0M = "0m";
	// --- 原子串与着色后的子串
	private String sourceText = "";
	private String styledText = "";
	// --- 字符串的前缀与后缀
	private String prefix = "";
	private String postfix = "";
	// --- 为每一个被着色的字符串设置分隔符
	private String delimiter;
	// --- ANSI escape code
	private final Set<Object> codes = new HashSet<>(1);
	private final Set<RGB> rgbCodes = new LinkedHashSet<>(1);

	/**
	 * 记录前后子串
	 */
	private ConsoleStr pre = null;
	private ConsoleStr next = null;

	public ConsoleStr() {
	}

	public ConsoleStr(String sourceText) {
		this.sourceText = sourceText;
	}

	/**
	 * 翻转字体颜色与背景颜色
	 */
	public ConsoleStr reverse(String... text) {
		return this.returnStr(FontColor.REVERSE, text);
	}

	// +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-
	// +                        foreground color                                  -+-
	// +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-
	// 子串颜色/背景色设置为黑色
	public ConsoleStr black(String... text) {
		return this.returnStr(FontColor.BLACK, text);
	}
	public ConsoleStr brightBlack(String... text) {
		return this.returnStr(FontColor.BRIGHT_BLACK, text);
	}
	public ConsoleStr bgmBlack(String... text) {
		return this.returnStr(FontColor.BACKGROUND_BLACK, text);
	}

	// 子串颜色/背景色设置为红色
	public ConsoleStr red(String... text) {
		return this.returnStr(FontColor.RED, text);
	}
	public ConsoleStr brightRed(String... text) {
		return this.returnStr(FontColor.BRIGHT_RED, text);
	}
	public ConsoleStr bgmRed(String... text) {
		return this.returnStr(FontColor.BACKGROUND_RED, text);
	}

	// 子串颜色/背景色设置为绿色
	public ConsoleStr green(String... text) {
		return this.returnStr(FontColor.GREEN, text);
	}
	public ConsoleStr brightGreen(String... text) {
		return this.returnStr(FontColor.BRIGHT_GREEN, text);
	}
	public ConsoleStr bgmGreen(String... text) {
		return this.returnStr(FontColor.BACKGROUND_GREEN, text);
	}

	// 子串颜色/背景色设置为黄色
	public ConsoleStr yellow(String... text) {
		return this.returnStr(FontColor.YELLOW, text);
	}
	public ConsoleStr brightYellow(String... text) {
		return this.returnStr(FontColor.BRIGHT_YELLOW, text);
	}
	public ConsoleStr bgmYellow(String... text) {
		return this.returnStr(FontColor.BACKGROUND_YELLOW, text);
	}

	// 子串颜色/背景色设置为蓝色
	public ConsoleStr blue(String... text) {
		return this.returnStr(FontColor.BLUE, text);
	}
	public ConsoleStr brightBlue(String... text) {
		return this.returnStr(FontColor.BRIGHT_BLUE, text);
	}
	public ConsoleStr bgmBlue(String... text) {
		return this.returnStr(FontColor.BACKGROUND_BLUE, text);
	}

	// 子串颜色/背景色设置为紫色
	public ConsoleStr purple(String... text) {
		return this.returnStr(FontColor.PURPLE, text);
	}
	public ConsoleStr brightPurple(String... text) {
		return this.returnStr(FontColor.BRIGHT_PURPLE, text);
	}
	public ConsoleStr bgmPurple(String... text) {
		return this.returnStr(FontColor.BACKGROUND_PURPLE, text);
	}

	// 子串颜色/背景色设置为青色
	public ConsoleStr cyan(String... text) {
		return this.returnStr(FontColor.CYAN, text);
	}
	public ConsoleStr brightCyan(String... text) {
		return this.returnStr(FontColor.BRIGHT_CYAN, text);
	}
	public ConsoleStr bgmCyan(String... text) {
		return this.returnStr(FontColor.BACKGROUND_CYAN, text);
	}

	// 子串颜色/背景色设置为白色
	public ConsoleStr white(String... text) {
		return this.returnStr(FontColor.WHITE, text);
	}
	public ConsoleStr brightWhite(String... text) {
		return this.returnStr(FontColor.BRIGHT_WHITE, text);
	}
	public ConsoleStr bgmWhite(String... text) {
		return this.returnStr(FontColor.BACKGROUND_WHITE, text);
	}

	/**
	 * 子串颜色设置为指定的 RGB 颜色
	 */
	public ConsoleStr color(int R, int G, int B) {
		validateRgbRange(R,G,B);
		RGB rgb = new RGB(R, G, B);
		rgb.fontColor = FontColor.RGB_FOREGROUND;
		this.rgbCodes.remove(rgb);
		this.rgbCodes.add(rgb);
		return this.returnStr(null);
	}
	/**
	 * 子串背景色设置为指定的 RGB 颜色
	 */
	public ConsoleStr background(int R, int G, int B) {
		validateRgbRange(R,G,B);
		RGB rgb = new RGB(R, G, B);
		rgb.fontColor = FontColor.RGB_BACKGROUND;
		this.rgbCodes.remove(rgb);
		this.rgbCodes.add(rgb);
		return this.returnStr(null);
	}
	/**
	 * 子串颜色设置为指定的 RGB 颜色
	 */
	public ConsoleStr color(RGB rgb) {
		rgb.fontColor = FontColor.RGB_FOREGROUND;
		this.rgbCodes.remove(rgb);
		this.rgbCodes.add(rgb);
		return this.returnStr(null);
	}
	/**
	 * 子串背景色设置为指定的 RGB 颜色
	 */
	public ConsoleStr background(RGB rgb) {
		rgb.fontColor = FontColor.RGB_BACKGROUND;
		this.rgbCodes.remove(rgb);
		this.rgbCodes.add(rgb);
		return this.returnStr(null);
	}

	// +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-
	// +                            style                                         -+-
	// +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-
	/**
	 * 加粗
	 */
	public ConsoleStr bold(String... text) {
		return this.returnStr(FontStyle.BOLD, text);
	}
	/**
	 * 斜体
	 */
	public ConsoleStr italics(String... text) {
		return this.returnStr(FontStyle.ITALICS, text);
	}
	/**
	 * 下划线
	 */
	public ConsoleStr underline(String... text) {
		return this.returnStr(FontStyle.UNDERLINE, text);
	}
	/**
	 * 双下划线
	 */
	public ConsoleStr doubleUnderline(String... text) {
		return this.returnStr(FontStyle.DOUBLE_UNDERLINE, text);
	}

	/**
	 * 删除线
	 */
	public ConsoleStr crossed(String... text) {
		return this.returnStr(FontStyle.CROSSED, text);
	}

	/**
	 * 取消删除线
	 */
	public ConsoleStr notCrossedOut(String... text) {
		return this.returnStr(FontStyle.NOT_CROSSED_OUT, text);
	}

	/**
	 * 框线
	 */
	public ConsoleStr framed(String... text) {
		return this.returnStr(FontStyle.FRAMED, text);
	}

	private ConsoleStr returnStr(Enum<?> style, String... text) {
		if(alwaysReturnNewOne){
			ConsoleStr another = another();
			if (text.length == 1)
				another.sourceText = this.prefix + text[0] + this.postfix;
			if (style != null)
				another.codes.add(style);
			return another;
		}
		// 不需要返回新的实例就就在当前子串上设置
		if (text.length == 1)
			this.sourceText = this.prefix + text[0] + this.postfix;
		if (style != null)
			this.codes.add(style);
		return this;
	}

	/**
	 * 设置任意的 ANSI escape code
	 */
	public ConsoleStr every(String code, String... text) {
		this.sourceText = text.length == 1 ? text[0] : "";
		this.codes.clear();
		this.codes.add(code);
		return alwaysReturnNewOne ? another() : this;
	}

	/**
	 * 新增一个子串，新增的子串样式会继承上一个子串的样式，取决于 transitivity 是否开启
	 */
	public ConsoleStr append(String text) {
		ConsoleStr consoleFont = ConsoleStr.newInstanceFrom(this, this.transitivity);
		consoleFont.sourceText = this.prefix + text + this.postfix;
		return consoleFont;
	}

	/**
	 * 新增一个子串，新增的子串样式不会继承上一个子串的样式
	 */
	public ConsoleStr another(String... text) {
		ConsoleStr newInstance = new ConsoleStr();

		this.next = newInstance;
		newInstance.pre = this;

		newInstance.alwaysReturnNewOne = this.alwaysReturnNewOne;
		newInstance.transitivity = this.transitivity;
		newInstance.prefix = this.prefix;
		newInstance.postfix = this.postfix;
		newInstance.delimiter = this.delimiter;

		newInstance.sourceText = text.length == 1 ?
				this.prefix + text[0] + this.postfix :
				this.prefix + this.postfix;

		return newInstance;
	}

	public ConsoleStr next(ConsoleStr next){
		this.next = next;
		next.pre = this;
		return next;
	}

	// +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-
	// +                            wrap                                          -+-
	// +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-
	/**
	 * 对子串进行着色处理，并为 styledText 设置着色后的字符串值
	 */
	private ConsoleStr wrap() {
		String codeStr = codes.stream().map(it -> {
			try {
				return it.toString();
			} catch (Exception e) {
				return (String) it;
			}
		}).collect(Collectors.joining(";"));
		// 将 rbg 值放到控制码的最后位置
		String rgbCodeStr = rgbCodes.stream().flatMap(it->it.rgbCodes().stream()).map(it->{
			try {
				return it.toString();
			} catch (Exception e) {
				return (String)it;
			}
		}).collect(Collectors.joining(";"));
		codeStr = codeStr.length() > 0 ?
				(rgbCodeStr.length() > 0 ? codeStr + ";" + rgbCodeStr : codeStr)
				: rgbCodeStr;
		this.styledText = WRAPPER + codeStr + M + this.sourceText + WRAPPER + $0M;
		return this;
	}

	private static ConsoleStr newInstanceFrom(ConsoleStr consoleFont, boolean transitivity) {
		ConsoleStr newInstance = new ConsoleStr();
		newInstance.pre = consoleFont;
		consoleFont.next = newInstance;

		newInstance.prefix = consoleFont.prefix;
		newInstance.postfix = consoleFont.postfix;
		newInstance.delimiter = consoleFont.delimiter;

		if (transitivity) {
			newInstance.codes.addAll(consoleFont.codes);
			newInstance.rgbCodes.addAll(consoleFont.rgbCodes);
			newInstance.transitivity = true;
			newInstance.alwaysReturnNewOne = consoleFont.alwaysReturnNewOne;
		}
		return newInstance;
	}

	public static ConsoleStr newInstance() {
		return new ConsoleStr();
	}

	/**
	 * 切换 transitivity 开关，设置后续新增的子串是否继承当前子串的样式
	 */
	public ConsoleStr toggleTransitivity() {
		this.transitivity = !this.transitivity;
		return this;
	}
	public ConsoleStr toggleTransitivity(boolean transitivity) {
		this.transitivity = transitivity;
		return this;
	}
	/**
	 * 切换 alwaysReturnNewOne 开关，设置添加样式后是否总是返回新的 ConsoleStr 实例
	 */
	public ConsoleStr toggleAlwaysReturnNewOne( ) {
		this.alwaysReturnNewOne = !this.alwaysReturnNewOne;
		return this;
	}
	public ConsoleStr toggleAlwaysReturnNewOne(boolean alwaysReturnNewOne) {
		this.alwaysReturnNewOne = alwaysReturnNewOne;
		return this;
	}

	//++++++++++++++++++++++++++++++++++++++++++++++++
	//+             ANSI escape code 常量值           +
	//++++++++++++++++++++++++++++++++++++++++++++++++
	private enum FontStyle {
		CROSSED("9"), NOT_CROSSED_OUT("29"), DOUBLE_UNDERLINE("21"), FRAMED("51"),
		BOLD("1"), ITALICS("3"), UNDERLINE("4"), RESET(M);
		private final String code;

		FontStyle(String code) {
			this.code = code;
		}

		@Override
		public String toString() {
			return code;
		}
	}

	private enum FontColor {
		REVERSE("7"),
		BLACK("30"), BRIGHT_BLACK("90"), BACKGROUND_BLACK("40"),
		RED("31"), BRIGHT_RED("91"), BACKGROUND_RED("41"),
		GREEN("32"), BRIGHT_GREEN("92"), BACKGROUND_GREEN("42"),
		YELLOW("33"), BRIGHT_YELLOW("93"), BACKGROUND_YELLOW("43"),
		BLUE("34"), BRIGHT_BLUE("94"), BACKGROUND_BLUE("44"),
		PURPLE("35"), BRIGHT_PURPLE("95"), BACKGROUND_PURPLE("45"),
		CYAN("36"), BRIGHT_CYAN("96"), BACKGROUND_CYAN("46"),
		WHITE("37"), BRIGHT_WHITE("97"), BACKGROUND_WHITE("47"),
		QUIET("39"),
		RGB_FOREGROUND("38;2"), RGB_BACKGROUND("48;2");
		private final String code;

		FontColor(String code) {
			this.code = code;
		}

		@Override
		public String toString() {
			return code;
		}
	}

	private enum Cursor {
		TWINKLE("5");
		private final String code;

		Cursor(String code) {
			this.code = code;
		}

		@Override
		public String toString() {
			return code;
		}
	}

	public static class RGB{
		private FontColor fontColor;
		private final int R,G,B;
		private String name;
		public RGB(int r, int g, int b,String name) {
			this(r,g,b);
			this.name = name;
		}
		public RGB(int r, int g, int b) {
			validateRgbRange(r,g,b);
			R = r;
			G = g;
			B = b;
		}
		public List<Object> rgbCodes(){
			return Arrays.asList(fontColor,R,G,B);
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			RGB rgb = (RGB) o;
			return fontColor == rgb.fontColor;
		}

		@Override
		public int hashCode() {
			return Objects.hash(fontColor);
		}

		@Override
		public String toString() {
			return this.name != null ? this.name : R + "," + G + "," + B;
		}
	}
	private static void validateRgbRange(int R, int G, int B) {
		if (0 > R || 255 < R) {
			throw new RuntimeException("R【" + R + "】值范围为【0-255】");
		}
		if (0 > G || 255 < G) {
			throw new RuntimeException("G【" + G + "】值范围为【0-255】");
		}
		if (0 > B || 255 < B) {
			throw new RuntimeException("B【" + B + "】值范围为【0-255】");
		}
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public void setDelimiter(String delimiter) {
		this.delimiter = delimiter;
	}

	public void setPostfix(String postfix) {
		this.postfix = postfix;
	}

	/**
	 * 返回着色后的字符串
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		ConsoleStr pre = this.pre == null ? this : this.pre;
		while (pre.pre != null) {
			pre = pre.pre;
		}
		for (ConsoleStr next = pre; next != null; next = next.next) {
			sb.append(next.wrap().styledText);

			if (this.delimiter != null && next.next != null &&
					// 若不总是返回前一个则不需要再次判断下下一个是否存在
					(!this.alwaysReturnNewOne || next.next.next != null))
				sb.append(this.delimiter);
		}
		return sb.toString();
	}
	/**
	 * 返回原始字符串
	 */
	public String toOriginalStr() {
		ConsoleStr pre = this.pre == null ? this : this.pre;
		while (pre.pre != null) {
			pre = pre.pre;
		}
		StringBuilder sb = new StringBuilder();
		for (ConsoleStr next = pre; next != null; next = next.next) {
			sb.append(next.sourceText);
		}
		return sb.toString();
	}
	/**
	 * 返回着色后的转义字符串
	 */
	public String toEscapeString() {
		return this.toString().replaceAll("\033", "esc");
	}



}