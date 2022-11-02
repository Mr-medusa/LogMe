package red.medusa.logme;

import red.medusa.logme.color.ConsoleStr;
import red.medusa.logme.format.LogFormat;
import red.medusa.logme.logable.*;
import red.medusa.logme.logable.message.ParamMsg;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * @author Mr.Medusa
 * @date 2022/6/6
 */
public class LogMe extends SubjectFactory {
    private int subjectId = 1;
    private LogFormat logFormat = LogFormat.DEFAULT_LOG_FORMAT;
    private final LinkedList<ConsoleStr> allLines = new LinkedList<>();
    private final Subject root;

    public LogMe(Subject root) {
        super();
        this.root = root;
    }

    public synchronized LogLine i2(String msg, Object... args) {
        if (args == null || args.length == 0) {
            return this.i(msg, 3);
        }
        StringBuilder sb = new StringBuilder();
        int[] segmentIndex = new int[args.length];
        int index = -1;
        int count = 0;
        while ((index = msg.indexOf("{}", index + 1)) != -1) {
            segmentIndex[count++] = index;
        }
        for (int i = 0; i < segmentIndex.length; i++) {
            if (i == 0) {
                sb.append(msg, 0, segmentIndex[i]);
            } else {
                sb.append(msg, segmentIndex[i - 1] + 2, segmentIndex[i]);
            }
            sb.append(args[i]);
        }
        if (segmentIndex[segmentIndex.length - 1] < msg.length() - 1) {
            sb.append(msg, segmentIndex[segmentIndex.length - 1] + 2, msg.length())
                    .append(args[args.length - 1]);
        } else {
            sb.append(args[args.length - 1]);
        }
        return this.i(sb.toString(), 3);
    }

    /**
     * @param msg
     * @param params 0:Emoji 1: Emoji
     * @return
     */
    public synchronized LogLine i(Object msg, boolean... params) {
        Subject subject;
        LogLine logLine = this.getLogContext().getLogLine();
        if (logLine == null) {
            subject = LOGME_DEFAULT_SUBJECT_MAP.get(this);
            if (subject == null) {
                throw new IllegalArgumentException("Subject was initiated");
            }
        } else {
            subject = logLine.getSubject();
        }
        return i(subject, msg, 2, null, params);
    }

    public synchronized LogLine i(Object msg, Integer intent, boolean... params) {
        Subject subject;
        LogLine logLine = this.getLogContext().getLogLine();
        if (logLine == null) {
            subject = LOGME_DEFAULT_SUBJECT_MAP.get(this);
            if (subject == null) {
                throw new IllegalArgumentException("Subject was initiated");
            }
        } else {
            subject = logLine.getSubject();
        }
        return i(subject, msg, intent, null, params);
    }

    public synchronized LogLine i(Subject subject, Object msg, boolean... params) {
        return i(subject, msg, 2, null, params);
    }

    /**
     * @see LogLine#prepareChildren(String...)
     */
    public synchronized LogLine i(Subject subject, Object msg, int deep, Integer indent, boolean... params) {
        if (subject == null) {
            throw new IllegalArgumentException("Subject must not be null");
        }
        if (this.getClose() != null && this.getClose()) {
            return new LogLine(null, null);
        }
        LogThreadHolder o = create(subject, msg, new Throwable().getStackTrace(), deep, params);
        if (indent != null) {
            // 因为先执行的 childParameterI,所以这里获取到的 intent 需要减1获取到正确的 intent
            o.setIndent(indent - 1);
        } else {
            o.setIndent(subject.getIndent());
        }

        if (subject.getColor() == null) {
            throw new IllegalArgumentException("Subject 已经存在: " + subject.getName());
        }
        subject.addLog(o);
        return new LogLine(subject, this);
    }

    public synchronized LogLine childI(String msg, boolean... params) {
        // 添加到子级
        return i(this.getLogContext().getLogLine().getSubject(), msg, 2, null, params);
    }


    public synchronized LogLine childParameterI(ParamMsg paramMsg, boolean... params) {
        Integer indent = null;
        if (this.getLogContext().containsParameter(paramMsg.getParam())) {
            indent = this.getLogContext().getParameterLogLine(paramMsg.getParam()).getSubject().getIndent();
        }
        return i(this.getLogContext().getLogLine().getSubject(), paramMsg, 2, indent, params);
    }

    public void print() {
        doPrint(null, null);
    }

    public void print(Subject subject) {
        doPrint(subject, null);
    }

    public void print(Thread thread) {
        doPrint(null, thread);
    }

    public void print(Subject subject, Thread thread) {
        doPrint(subject, thread);
    }

    public void doPrint(Subject subject, Thread thread) {
        if ((this.getClose() != null && this.getClose())) {
            return;
        }
        if (subject == null) {
            // this.logFormat.printSubjectLog(root, thread);
            for (Subject child : root.getChildren()) {
                printSubject(child, thread);
            }
        } else {
            printSubject(subject, thread);
        }
    }

    /**
     * @see LogMe#childParameterI
     * <p>
     * 清除程序某一执行块中的参数信息
     */
    public void withParamContext(Runnable runnable) {
        this.getLogContext().clearParameter();
        runnable.run();
        this.getLogContext().clearParameter();
    }

    public LogMe clearParameter() {
        this.getLogContext().clearParameter();
        return this;
    }

    public void printAll() {
        for (ConsoleStr consoleStr : allLines) {
            System.out.println(consoleStr);
        }
    }

    private void printSubject(Subject subject, Thread thread) {
        if (subject.getIndent() >= this.getMaxStackVal()) {
            this.logFormat.stackOver(subject.getIndent());
            return;
        }
        this.logFormat.printSubjectLog(subject, thread);
        List<Logable> lines = subject.getLogLines();
        lines.forEach(it -> {
            if (it instanceof LogThreadHolder) {
                if (thread == null || ((LogThreadHolder) it).getThread() == thread) {
                    this.logFormat.printSubject(((LogThreadHolder) it).getIndent(), it);
                }
            } else {
                printSubject(((LogLine) it).getSubject(), thread);
            }
        });
        if (!subject.getChildren().isEmpty()) {
            for (Subject child : subject.getChildren()) {
                printSubject(child, thread);
            }
        }
    }

    private LogThreadHolder create(Subject subject, Object msg, StackTraceElement[] stackTrace, int deep, boolean... params) {
        String trace = stackTrace[deep].toString();
        return this.logFormat.format(trace, subject, params, msg, this);
    }

    public Subject getRoot() {
        return root;
    }

    public void setChild(LogMe child) {
        this.root.getChildren().add(child.root);
    }

    /*
     *  color
     */
    public static ConsoleStr.RGB randomColor() {
        return colors.get(random.nextInt(colors.size()));
    }

    private static final Random random = new Random();
    public static final List<ConsoleStr.RGB> colors = new ArrayList<>();

    static {
        // https://www.917118.com/tool/color_3.html
        colors.add(new ConsoleStr.RGB(255, 250, 250, "Snow"));
        colors.add(new ConsoleStr.RGB(248, 248, 255, "GhostWhite"));
        colors.add(new ConsoleStr.RGB(245, 245, 245, "WhiteSmoke"));
        colors.add(new ConsoleStr.RGB(220, 220, 220, "Gainsboro"));
        colors.add(new ConsoleStr.RGB(255, 250, 240, "FloralWhite"));
        colors.add(new ConsoleStr.RGB(253, 245, 230, "OldLace"));
        colors.add(new ConsoleStr.RGB(250, 240, 230, "Linen"));
        colors.add(new ConsoleStr.RGB(250, 235, 215, "AntiqueWhite"));
        colors.add(new ConsoleStr.RGB(255, 239, 213, "PapayaWhip"));
        colors.add(new ConsoleStr.RGB(255, 235, 205, "BlanchedAlmond"));
        colors.add(new ConsoleStr.RGB(255, 228, 196, "Bisque"));
        colors.add(new ConsoleStr.RGB(255, 218, 185, "PeachPuff"));
        colors.add(new ConsoleStr.RGB(255, 222, 173, "NavajoWhite"));
        colors.add(new ConsoleStr.RGB(255, 228, 181, "Moccasin"));
        colors.add(new ConsoleStr.RGB(255, 248, 220, "Cornsilk"));
        colors.add(new ConsoleStr.RGB(255, 255, 240, "Ivory"));
        colors.add(new ConsoleStr.RGB(255, 250, 205, "LemonChiffon"));
        colors.add(new ConsoleStr.RGB(255, 245, 238, "Seashell"));
        colors.add(new ConsoleStr.RGB(240, 255, 240, "Honeydew"));
        colors.add(new ConsoleStr.RGB(245, 255, 250, "MintCream"));
        colors.add(new ConsoleStr.RGB(240, 255, 255, "Azure"));
        colors.add(new ConsoleStr.RGB(240, 248, 255, "AliceBlue"));
        colors.add(new ConsoleStr.RGB(230, 230, 250, "lavender"));
        colors.add(new ConsoleStr.RGB(255, 240, 245, "LavenderBlush"));
        colors.add(new ConsoleStr.RGB(255, 228, 225, "MistyRose"));
        colors.add(new ConsoleStr.RGB(47, 79, 79, "DarkSlateGray"));
        colors.add(new ConsoleStr.RGB(105, 105, 105, "DimGrey"));
        colors.add(new ConsoleStr.RGB(112, 128, 144, "SlateGrey"));
        colors.add(new ConsoleStr.RGB(119, 136, 153, "LightSlateGray"));
        colors.add(new ConsoleStr.RGB(190, 190, 190, "Grey"));
        colors.add(new ConsoleStr.RGB(211, 211, 211, "LightGray"));
        colors.add(new ConsoleStr.RGB(25, 25, 112, "MidnightBlue"));
        colors.add(new ConsoleStr.RGB(0, 0, 128, "NavyBlue"));
        colors.add(new ConsoleStr.RGB(100, 149, 237, "CornflowerBlue"));
        colors.add(new ConsoleStr.RGB(72, 61, 139, "DarkSlateBlue"));
        colors.add(new ConsoleStr.RGB(106, 90, 205, "SlateBlue"));
        colors.add(new ConsoleStr.RGB(123, 104, 238, "MediumSlateBlue"));
        colors.add(new ConsoleStr.RGB(132, 112, 255, "LightSlateBlue"));
        colors.add(new ConsoleStr.RGB(0, 0, 205, "MediumBlue"));
        colors.add(new ConsoleStr.RGB(65, 105, 225, "RoyalBlue"));
        colors.add(new ConsoleStr.RGB(0, 0, 255, "Blue"));
        colors.add(new ConsoleStr.RGB(30, 144, 255, "DodgerBlue"));
        colors.add(new ConsoleStr.RGB(0, 191, 255, "DeepSkyBlue"));
        colors.add(new ConsoleStr.RGB(135, 206, 235, "SkyBlue"));
        colors.add(new ConsoleStr.RGB(135, 206, 250, "LightSkyBlue"));
        colors.add(new ConsoleStr.RGB(70, 130, 180, "SteelBlue"));
        colors.add(new ConsoleStr.RGB(176, 196, 222, "LightSteelBlue"));
        colors.add(new ConsoleStr.RGB(173, 216, 230, "LightBlue"));
        colors.add(new ConsoleStr.RGB(176, 224, 230, "PowderBlue"));
        colors.add(new ConsoleStr.RGB(175, 238, 238, "PaleTurquoise"));
        colors.add(new ConsoleStr.RGB(0, 206, 209, "DarkTurquoise"));
        colors.add(new ConsoleStr.RGB(72, 209, 204, "MediumTurquoise"));
        colors.add(new ConsoleStr.RGB(64, 224, 208, "Turquoise"));
        colors.add(new ConsoleStr.RGB(0, 255, 255, "Cyan"));
        colors.add(new ConsoleStr.RGB(224, 255, 255, "LightCyan"));
        colors.add(new ConsoleStr.RGB(95, 158, 160, "CadetBlue"));
        colors.add(new ConsoleStr.RGB(102, 205, 170, "MediumAquamarine"));
        colors.add(new ConsoleStr.RGB(127, 255, 212, "Aquamarine"));
        colors.add(new ConsoleStr.RGB(0, 100, 0, "DarkGreen"));
        colors.add(new ConsoleStr.RGB(85, 107, 47, "DarkOliveGreen"));
        colors.add(new ConsoleStr.RGB(143, 188, 143, "DarkSeaGreen"));
        colors.add(new ConsoleStr.RGB(46, 139, 87, "SeaGreen"));
        colors.add(new ConsoleStr.RGB(60, 179, 113, "MediumSeaGreen"));
        colors.add(new ConsoleStr.RGB(32, 178, 170, "LightSeaGreen"));
        colors.add(new ConsoleStr.RGB(152, 251, 152, "PaleGreen"));
        colors.add(new ConsoleStr.RGB(0, 255, 127, "SpringGreen"));
        colors.add(new ConsoleStr.RGB(124, 252, 0, "LawnGreen"));
        colors.add(new ConsoleStr.RGB(0, 255, 0, "Green"));
        colors.add(new ConsoleStr.RGB(127, 255, 0, "Chartreuse"));
        colors.add(new ConsoleStr.RGB(0, 250, 154, "MedSpringGreen"));
        colors.add(new ConsoleStr.RGB(173, 255, 47, "GreenYellow"));
        colors.add(new ConsoleStr.RGB(50, 205, 50, "LimeGreen"));
        colors.add(new ConsoleStr.RGB(154, 205, 50, "YellowGreen"));
        colors.add(new ConsoleStr.RGB(34, 139, 34, "ForestGreen"));
        colors.add(new ConsoleStr.RGB(107, 142, 35, "OliveDrab"));
        colors.add(new ConsoleStr.RGB(189, 183, 107, "DarkKhaki"));
        colors.add(new ConsoleStr.RGB(238, 232, 170, "PaleGoldenrod"));
        colors.add(new ConsoleStr.RGB(250, 250, 210, "LtGoldenrodYello"));
        colors.add(new ConsoleStr.RGB(255, 255, 224, "LightYellow"));
        colors.add(new ConsoleStr.RGB(255, 255, 0, "Yellow"));
        colors.add(new ConsoleStr.RGB(255, 215, 0, "Gold"));
        colors.add(new ConsoleStr.RGB(238, 221, 130, "LightGoldenrod"));
        colors.add(new ConsoleStr.RGB(218, 165, 32, "goldenrod"));
        colors.add(new ConsoleStr.RGB(184, 134, 11, "DarkGoldenrod"));
        colors.add(new ConsoleStr.RGB(205, 92, 92, "IndianRed"));
        colors.add(new ConsoleStr.RGB(160, 82, 45, "Sienna"));
        colors.add(new ConsoleStr.RGB(205, 133, 63, "Peru"));
        colors.add(new ConsoleStr.RGB(222, 184, 135, "Burlywood"));
        colors.add(new ConsoleStr.RGB(245, 245, 220, "Beige"));
        colors.add(new ConsoleStr.RGB(245, 222, 179, "Wheat"));
        colors.add(new ConsoleStr.RGB(244, 164, 96, "SandyBrown"));
        colors.add(new ConsoleStr.RGB(210, 180, 140, "Tan"));
        colors.add(new ConsoleStr.RGB(210, 105, 30, "Chocolate"));
        colors.add(new ConsoleStr.RGB(178, 34, 34, "Firebrick"));
        colors.add(new ConsoleStr.RGB(165, 42, 42, "Brown"));
        colors.add(new ConsoleStr.RGB(233, 150, 122, "DarkSalmon"));
        colors.add(new ConsoleStr.RGB(250, 128, 114, "Salmon"));
        colors.add(new ConsoleStr.RGB(255, 160, 122, "LightSalmon"));
        colors.add(new ConsoleStr.RGB(255, 165, 0, "Orange"));
        colors.add(new ConsoleStr.RGB(255, 140, 0, "DarkOrange"));
        colors.add(new ConsoleStr.RGB(255, 127, 80, "Coral"));
        colors.add(new ConsoleStr.RGB(240, 128, 128, "LightCoral"));
        colors.add(new ConsoleStr.RGB(255, 99, 71, "Tomato"));
        colors.add(new ConsoleStr.RGB(255, 69, 0, "OrangeRed"));
        colors.add(new ConsoleStr.RGB(255, 0, 0, "Red"));
        colors.add(new ConsoleStr.RGB(255, 105, 180, "HotPink"));
        colors.add(new ConsoleStr.RGB(255, 20, 147, "DeepPink"));
        colors.add(new ConsoleStr.RGB(255, 192, 203, "Pink"));
        colors.add(new ConsoleStr.RGB(255, 182, 193, "LightPink"));
        colors.add(new ConsoleStr.RGB(219, 112, 147, "PaleVioletRed"));
        colors.add(new ConsoleStr.RGB(176, 48, 96, "Maroon"));
        colors.add(new ConsoleStr.RGB(199, 21, 133, "MediumVioletRed"));
        colors.add(new ConsoleStr.RGB(208, 32, 144, "VioletRed"));
        colors.add(new ConsoleStr.RGB(255, 0, 255, "Magenta"));
        colors.add(new ConsoleStr.RGB(238, 130, 238, "Violet"));
        colors.add(new ConsoleStr.RGB(221, 160, 221, "Plum"));
        colors.add(new ConsoleStr.RGB(218, 112, 214, "Orchid"));
        colors.add(new ConsoleStr.RGB(186, 85, 211, "MediumOrchid"));
        colors.add(new ConsoleStr.RGB(153, 50, 204, "DarkOrchid"));
        colors.add(new ConsoleStr.RGB(148, 0, 211, "DarkViolet"));
        colors.add(new ConsoleStr.RGB(138, 43, 226, "BlueViolet"));
        colors.add(new ConsoleStr.RGB(160, 32, 240, "Purple"));
        colors.add(new ConsoleStr.RGB(147, 112, 219, "MediumPurple"));
        colors.add(new ConsoleStr.RGB(216, 191, 216, "Thistle"));
        colors.add(new ConsoleStr.RGB(255, 250, 250, "Snow1"));
        colors.add(new ConsoleStr.RGB(238, 233, 233, "Snow2"));
        colors.add(new ConsoleStr.RGB(205, 201, 201, "Snow3"));
        colors.add(new ConsoleStr.RGB(139, 137, 137, "Snow4"));
        colors.add(new ConsoleStr.RGB(255, 245, 238, "Seashell1"));
        colors.add(new ConsoleStr.RGB(238, 229, 222, "Seashell2"));
        colors.add(new ConsoleStr.RGB(205, 197, 191, "Seashell3"));
        colors.add(new ConsoleStr.RGB(139, 134, 130, "Seashell4"));
        colors.add(new ConsoleStr.RGB(255, 239, 219, "AntiqueWhite1"));
        colors.add(new ConsoleStr.RGB(238, 223, 204, "AntiqueWhite2"));
        colors.add(new ConsoleStr.RGB(205, 192, 176, "AntiqueWhite3"));
        colors.add(new ConsoleStr.RGB(139, 131, 120, "AntiqueWhite4"));
        colors.add(new ConsoleStr.RGB(255, 228, 196, "Bisque1"));
        colors.add(new ConsoleStr.RGB(238, 213, 183, "Bisque2"));
        colors.add(new ConsoleStr.RGB(205, 183, 158, "Bisque3"));
        colors.add(new ConsoleStr.RGB(139, 125, 107, "Bisque4"));
        colors.add(new ConsoleStr.RGB(255, 218, 185, "PeachPuff1"));
        colors.add(new ConsoleStr.RGB(238, 203, 173, "PeachPuff2"));
        colors.add(new ConsoleStr.RGB(205, 175, 149, "PeachPuff3"));
        colors.add(new ConsoleStr.RGB(139, 119, 101, "PeachPuff4"));
        colors.add(new ConsoleStr.RGB(255, 222, 173, "NavajoWhite1"));
        colors.add(new ConsoleStr.RGB(238, 207, 161, "NavajoWhite2"));
        colors.add(new ConsoleStr.RGB(205, 179, 139, "NavajoWhite3"));
        colors.add(new ConsoleStr.RGB(139, 121, 94, "NavajoWhite4"));
        colors.add(new ConsoleStr.RGB(255, 250, 205, "LemonChiffon1"));
        colors.add(new ConsoleStr.RGB(238, 233, 191, "LemonChiffon2"));
        colors.add(new ConsoleStr.RGB(205, 201, 165, "LemonChiffon3"));
        colors.add(new ConsoleStr.RGB(139, 137, 112, "LemonChiffon4"));
        colors.add(new ConsoleStr.RGB(255, 248, 220, "Cornsilk1"));
        colors.add(new ConsoleStr.RGB(238, 232, 205, "Cornsilk2"));
        colors.add(new ConsoleStr.RGB(205, 200, 177, "Cornsilk3"));
        colors.add(new ConsoleStr.RGB(139, 136, 120, "Cornsilk4"));
        colors.add(new ConsoleStr.RGB(255, 255, 240, "Ivory1"));
        colors.add(new ConsoleStr.RGB(238, 238, 224, "Ivory2"));
        colors.add(new ConsoleStr.RGB(205, 205, 193, "Ivory3"));
        colors.add(new ConsoleStr.RGB(139, 139, 131, "Ivory4"));
        colors.add(new ConsoleStr.RGB(240, 255, 240, "Honeydew1"));
        colors.add(new ConsoleStr.RGB(224, 238, 224, "Honeydew2"));
        colors.add(new ConsoleStr.RGB(193, 205, 193, "Honeydew3"));
        colors.add(new ConsoleStr.RGB(131, 139, 131, "Honeydew4"));
        colors.add(new ConsoleStr.RGB(255, 240, 245, "LavenderBlush1"));
        colors.add(new ConsoleStr.RGB(238, 224, 229, "LavenderBlush2"));
        colors.add(new ConsoleStr.RGB(205, 193, 197, "LavenderBlush3"));
        colors.add(new ConsoleStr.RGB(139, 131, 134, "LavenderBlush4"));
        colors.add(new ConsoleStr.RGB(255, 228, 225, "MistyRose1"));
        colors.add(new ConsoleStr.RGB(238, 213, 210, "MistyRose2"));
        colors.add(new ConsoleStr.RGB(205, 183, 181, "MistyRose3"));
        colors.add(new ConsoleStr.RGB(139, 125, 123, "MistyRose4"));
        colors.add(new ConsoleStr.RGB(240, 255, 255, "Azure1"));
        colors.add(new ConsoleStr.RGB(224, 238, 238, "Azure2"));
        colors.add(new ConsoleStr.RGB(193, 205, 205, "Azure3"));
        colors.add(new ConsoleStr.RGB(131, 139, 139, "Azure4"));
        colors.add(new ConsoleStr.RGB(131, 111, 255, "SlateBlue1"));
        colors.add(new ConsoleStr.RGB(122, 103, 238, "SlateBlue2"));
        colors.add(new ConsoleStr.RGB(105, 89, 205, "SlateBlue3"));
        colors.add(new ConsoleStr.RGB(71, 60, 139, "SlateBlue4"));
        colors.add(new ConsoleStr.RGB(72, 118, 255, "RoyalBlue1"));
        colors.add(new ConsoleStr.RGB(67, 110, 238, "RoyalBlue2"));
        colors.add(new ConsoleStr.RGB(58, 95, 205, "RoyalBlue3"));
        colors.add(new ConsoleStr.RGB(39, 64, 139, "RoyalBlue4"));
        colors.add(new ConsoleStr.RGB(0, 0, 255, "Blue1"));
        colors.add(new ConsoleStr.RGB(0, 0, 238, "Blue2"));
        colors.add(new ConsoleStr.RGB(0, 0, 205, "Blue3"));
        colors.add(new ConsoleStr.RGB(0, 0, 139, "Blue4"));
        colors.add(new ConsoleStr.RGB(30, 144, 255, "DodgerBlue1"));
        colors.add(new ConsoleStr.RGB(28, 134, 238, "DodgerBlue2"));
        colors.add(new ConsoleStr.RGB(24, 116, 205, "DodgerBlue3"));
        colors.add(new ConsoleStr.RGB(16, 78, 139, "DodgerBlue4"));
        colors.add(new ConsoleStr.RGB(99, 184, 255, "SteelBlue1"));
        colors.add(new ConsoleStr.RGB(92, 172, 238, "SteelBlue2"));
        colors.add(new ConsoleStr.RGB(79, 148, 205, "SteelBlue3"));
        colors.add(new ConsoleStr.RGB(54, 100, 139, "SteelBlue4"));
        colors.add(new ConsoleStr.RGB(0, 191, 255, "DeepSkyBlue1"));
        colors.add(new ConsoleStr.RGB(0, 178, 238, "DeepSkyBlue2"));
        colors.add(new ConsoleStr.RGB(0, 154, 205, "DeepSkyBlue3"));
        colors.add(new ConsoleStr.RGB(0, 104, 139, "DeepSkyBlue4"));
        colors.add(new ConsoleStr.RGB(135, 206, 255, "SkyBlue1"));
        colors.add(new ConsoleStr.RGB(126, 192, 238, "SkyBlue2"));
        colors.add(new ConsoleStr.RGB(108, 166, 205, "SkyBlue3"));
        colors.add(new ConsoleStr.RGB(74, 112, 139, "SkyBlue4"));
        colors.add(new ConsoleStr.RGB(176, 226, 255, "LightSkyBlue1"));
        colors.add(new ConsoleStr.RGB(164, 211, 238, "LightSkyBlue2"));
        colors.add(new ConsoleStr.RGB(141, 182, 205, "LightSkyBlue3"));
        colors.add(new ConsoleStr.RGB(96, 123, 139, "LightSkyBlue4"));
        colors.add(new ConsoleStr.RGB(198, 226, 255, "SlateGray1"));
        colors.add(new ConsoleStr.RGB(185, 211, 238, "SlateGray2"));
        colors.add(new ConsoleStr.RGB(159, 182, 205, "SlateGray3"));
        colors.add(new ConsoleStr.RGB(108, 123, 139, "SlateGray4"));
        colors.add(new ConsoleStr.RGB(202, 225, 255, "LightSteelBlue1"));
        colors.add(new ConsoleStr.RGB(188, 210, 238, "LightSteelBlue2"));
        colors.add(new ConsoleStr.RGB(162, 181, 205, "LightSteelBlue3"));
        colors.add(new ConsoleStr.RGB(110, 123, 139, "LightSteelBlue4"));
        colors.add(new ConsoleStr.RGB(191, 239, 255, "LightBlue1"));
        colors.add(new ConsoleStr.RGB(178, 223, 238, "LightBlue2"));
        colors.add(new ConsoleStr.RGB(154, 192, 205, "LightBlue3"));
        colors.add(new ConsoleStr.RGB(104, 131, 139, "LightBlue4"));
        colors.add(new ConsoleStr.RGB(224, 255, 255, "LightCyan1"));
        colors.add(new ConsoleStr.RGB(209, 238, 238, "LightCyan2"));
        colors.add(new ConsoleStr.RGB(180, 205, 205, "LightCyan3"));
        colors.add(new ConsoleStr.RGB(122, 139, 139, "LightCyan4"));
        colors.add(new ConsoleStr.RGB(187, 255, 255, "PaleTurquoise1"));
        colors.add(new ConsoleStr.RGB(174, 238, 238, "PaleTurquoise2"));
        colors.add(new ConsoleStr.RGB(150, 205, 205, "PaleTurquoise3"));
        colors.add(new ConsoleStr.RGB(102, 139, 139, "PaleTurquoise4"));
        colors.add(new ConsoleStr.RGB(152, 245, 255, "CadetBlue1"));
        colors.add(new ConsoleStr.RGB(142, 229, 238, "CadetBlue2"));
        colors.add(new ConsoleStr.RGB(122, 197, 205, "CadetBlue3"));
        colors.add(new ConsoleStr.RGB(83, 134, 139, "CadetBlue4"));
        colors.add(new ConsoleStr.RGB(0, 245, 255, "Turquoise1"));
        colors.add(new ConsoleStr.RGB(0, 229, 238, "Turquoise2"));
        colors.add(new ConsoleStr.RGB(0, 197, 205, "Turquoise3"));
        colors.add(new ConsoleStr.RGB(0, 134, 139, "Turquoise4"));
        colors.add(new ConsoleStr.RGB(0, 255, 255, "Cyan1"));
        colors.add(new ConsoleStr.RGB(0, 238, 238, "Cyan2"));
        colors.add(new ConsoleStr.RGB(0, 205, 205, "Cyan3"));
        colors.add(new ConsoleStr.RGB(0, 139, 139, "Cyan4"));
        colors.add(new ConsoleStr.RGB(151, 255, 255, "DarkSlateGray1"));
        colors.add(new ConsoleStr.RGB(141, 238, 238, "DarkSlateGray2"));
        colors.add(new ConsoleStr.RGB(121, 205, 205, "DarkSlateGray3"));
        colors.add(new ConsoleStr.RGB(82, 139, 139, "DarkSlateGray4"));
        colors.add(new ConsoleStr.RGB(127, 255, 212, "Aquamarine1"));
        colors.add(new ConsoleStr.RGB(118, 238, 198, "Aquamarine2"));
        colors.add(new ConsoleStr.RGB(102, 205, 170, "Aquamarine3"));
        colors.add(new ConsoleStr.RGB(69, 139, 116, "Aquamarine4"));
        colors.add(new ConsoleStr.RGB(193, 255, 193, "DarkSeaGreen1"));
        colors.add(new ConsoleStr.RGB(180, 238, 180, "DarkSeaGreen2"));
        colors.add(new ConsoleStr.RGB(155, 205, 155, "DarkSeaGreen3"));
        colors.add(new ConsoleStr.RGB(105, 139, 105, "DarkSeaGreen4"));
        colors.add(new ConsoleStr.RGB(84, 255, 159, "SeaGreen1"));
        colors.add(new ConsoleStr.RGB(78, 238, 148, "SeaGreen2"));
        colors.add(new ConsoleStr.RGB(67, 205, 128, "SeaGreen3"));
        colors.add(new ConsoleStr.RGB(46, 139, 87, "SeaGreen4"));
        colors.add(new ConsoleStr.RGB(154, 255, 154, "PaleGreen1"));
        colors.add(new ConsoleStr.RGB(144, 238, 144, "PaleGreen2"));
        colors.add(new ConsoleStr.RGB(124, 205, 124, "PaleGreen3"));
        colors.add(new ConsoleStr.RGB(84, 139, 84, "PaleGreen4"));
        colors.add(new ConsoleStr.RGB(0, 255, 127, "SpringGreen1"));
        colors.add(new ConsoleStr.RGB(0, 238, 118, "SpringGreen2"));
        colors.add(new ConsoleStr.RGB(0, 205, 102, "SpringGreen3"));
        colors.add(new ConsoleStr.RGB(0, 139, 69, "SpringGreen4"));
        colors.add(new ConsoleStr.RGB(0, 255, 0, "Green1"));
        colors.add(new ConsoleStr.RGB(0, 238, 0, "Green2"));
        colors.add(new ConsoleStr.RGB(0, 205, 0, "Green3"));
        colors.add(new ConsoleStr.RGB(0, 139, 0, "Green4"));
        colors.add(new ConsoleStr.RGB(127, 255, 0, "Chartreuse1"));
        colors.add(new ConsoleStr.RGB(118, 238, 0, "Chartreuse2"));
        colors.add(new ConsoleStr.RGB(102, 205, 0, "Chartreuse3"));
        colors.add(new ConsoleStr.RGB(69, 139, 0, "Chartreuse4"));
        colors.add(new ConsoleStr.RGB(192, 255, 62, "OliveDrab1"));
        colors.add(new ConsoleStr.RGB(179, 238, 58, "OliveDrab2"));
        colors.add(new ConsoleStr.RGB(154, 205, 50, "OliveDrab3"));
        colors.add(new ConsoleStr.RGB(105, 139, 34, "OliveDrab4"));
        colors.add(new ConsoleStr.RGB(202, 255, 112, "DarkOliveGreen1"));
        colors.add(new ConsoleStr.RGB(188, 238, 104, "DarkOliveGreen2"));
        colors.add(new ConsoleStr.RGB(162, 205, 90, "DarkOliveGreen3"));
        colors.add(new ConsoleStr.RGB(110, 139, 61, "DarkOliveGreen4"));
        colors.add(new ConsoleStr.RGB(255, 246, 143, "Khaki1"));
        colors.add(new ConsoleStr.RGB(238, 230, 133, "Khaki2"));
        colors.add(new ConsoleStr.RGB(205, 198, 115, "Khaki3"));
        colors.add(new ConsoleStr.RGB(139, 134, 78, "Khaki4"));
        colors.add(new ConsoleStr.RGB(255, 236, 139, "LightGoldenrod1"));
        colors.add(new ConsoleStr.RGB(255, 255, 224, "LightYellow1"));
        colors.add(new ConsoleStr.RGB(238, 238, 209, "LightYellow2"));
        colors.add(new ConsoleStr.RGB(205, 205, 180, "LightYellow3"));
        colors.add(new ConsoleStr.RGB(139, 139, 122, "LightYellow4"));
        colors.add(new ConsoleStr.RGB(255, 255, 0, "Yellow1"));
        colors.add(new ConsoleStr.RGB(238, 238, 0, "Yellow2"));
        colors.add(new ConsoleStr.RGB(205, 205, 0, "Yellow3"));
        colors.add(new ConsoleStr.RGB(139, 139, 0, "Yellow4"));
        colors.add(new ConsoleStr.RGB(255, 215, 0, "Gold1"));
        colors.add(new ConsoleStr.RGB(238, 201, 0, "Gold2"));
        colors.add(new ConsoleStr.RGB(205, 173, 0, "Gold3"));
        colors.add(new ConsoleStr.RGB(139, 117, 0, "Gold4"));
        colors.add(new ConsoleStr.RGB(255, 193, 37, "Goldenrod1"));
        colors.add(new ConsoleStr.RGB(238, 180, 34, "Goldenrod2"));
        colors.add(new ConsoleStr.RGB(205, 155, 29, "Goldenrod3"));
        colors.add(new ConsoleStr.RGB(139, 105, 20, "Goldenrod4"));
        colors.add(new ConsoleStr.RGB(255, 185, 15, "DarkGoldenrod1"));
        colors.add(new ConsoleStr.RGB(238, 173, 14, "DarkGoldenrod2"));
        colors.add(new ConsoleStr.RGB(205, 149, 12, "DarkGoldenrod3"));
        colors.add(new ConsoleStr.RGB(139, 101, 8, "DarkGoldenrod4"));
        colors.add(new ConsoleStr.RGB(255, 193, 193, "RosyBrown1"));
        colors.add(new ConsoleStr.RGB(238, 180, 180, "RosyBrown2"));
        colors.add(new ConsoleStr.RGB(205, 155, 155, "RosyBrown3"));
        colors.add(new ConsoleStr.RGB(139, 105, 105, "RosyBrown4"));
        colors.add(new ConsoleStr.RGB(255, 106, 106, "IndianRed1"));
        colors.add(new ConsoleStr.RGB(238, 99, 99, "IndianRed2"));
        colors.add(new ConsoleStr.RGB(205, 85, 85, "IndianRed3"));
        colors.add(new ConsoleStr.RGB(139, 58, 58, "IndianRed4"));
        colors.add(new ConsoleStr.RGB(255, 130, 71, "Sienna1"));
        colors.add(new ConsoleStr.RGB(238, 121, 66, "Sienna2"));
        colors.add(new ConsoleStr.RGB(205, 104, 57, "Sienna3"));
        colors.add(new ConsoleStr.RGB(139, 71, 38, "Sienna4"));
        colors.add(new ConsoleStr.RGB(255, 211, 155, "Burlywood1"));
        colors.add(new ConsoleStr.RGB(238, 197, 145, "Burlywood2"));
        colors.add(new ConsoleStr.RGB(205, 170, 125, "Burlywood3"));
        colors.add(new ConsoleStr.RGB(139, 115, 85, "Burlywood4"));
        colors.add(new ConsoleStr.RGB(255, 231, 186, "Wheat1"));
        colors.add(new ConsoleStr.RGB(238, 216, 174, "Wheat2"));
        colors.add(new ConsoleStr.RGB(205, 186, 150, "Wheat3"));
        colors.add(new ConsoleStr.RGB(139, 126, 102, "Wheat4"));
        colors.add(new ConsoleStr.RGB(255, 165, 79, "Tan1"));
        colors.add(new ConsoleStr.RGB(238, 154, 73, "Tan2"));
        colors.add(new ConsoleStr.RGB(205, 133, 63, "Tan3"));
        colors.add(new ConsoleStr.RGB(139, 90, 43, "Tan4"));
        colors.add(new ConsoleStr.RGB(255, 127, 36, "Chocolate1"));
        colors.add(new ConsoleStr.RGB(238, 118, 33, "Chocolate2"));
        colors.add(new ConsoleStr.RGB(205, 102, 29, "Chocolate3"));
        colors.add(new ConsoleStr.RGB(139, 69, 19, "Chocolate4"));
        colors.add(new ConsoleStr.RGB(255, 48, 48, "Firebrick1"));
        colors.add(new ConsoleStr.RGB(238, 44, 44, "Firebrick2"));
        colors.add(new ConsoleStr.RGB(205, 38, 38, "Firebrick3"));
        colors.add(new ConsoleStr.RGB(139, 26, 26, "Firebrick4"));
        colors.add(new ConsoleStr.RGB(255, 64, 64, "Brown1"));
        colors.add(new ConsoleStr.RGB(238, 59, 59, "Brown2"));
        colors.add(new ConsoleStr.RGB(205, 51, 51, "Brown3"));
        colors.add(new ConsoleStr.RGB(139, 35, 35, "Brown4"));
        colors.add(new ConsoleStr.RGB(255, 140, 105, "Salmon1"));
        colors.add(new ConsoleStr.RGB(238, 130, 98, "Salmon2"));
        colors.add(new ConsoleStr.RGB(205, 112, 84, "Salmon3"));
        colors.add(new ConsoleStr.RGB(139, 76, 57, "Salmon4"));
        colors.add(new ConsoleStr.RGB(255, 160, 122, "LightSalmon1"));
        colors.add(new ConsoleStr.RGB(238, 149, 114, "LightSalmon2"));
        colors.add(new ConsoleStr.RGB(205, 129, 98, "LightSalmon3"));
        colors.add(new ConsoleStr.RGB(139, 87, 66, "LightSalmon4"));
        colors.add(new ConsoleStr.RGB(255, 165, 0, "Orange1"));
        colors.add(new ConsoleStr.RGB(255, 127, 0, "DarkOrange1"));
        colors.add(new ConsoleStr.RGB(238, 118, 0, "DarkOrange2"));
        colors.add(new ConsoleStr.RGB(205, 102, 0, "DarkOrange3"));
        colors.add(new ConsoleStr.RGB(139, 69, 0, "DarkOrange4"));
        colors.add(new ConsoleStr.RGB(255, 114, 86, "Coral1"));
        colors.add(new ConsoleStr.RGB(238, 106, 80, "Coral2"));
        colors.add(new ConsoleStr.RGB(205, 91, 69, "Coral3"));
        colors.add(new ConsoleStr.RGB(139, 62, 47, "Coral4"));
        colors.add(new ConsoleStr.RGB(255, 99, 71, "Tomato1"));
        colors.add(new ConsoleStr.RGB(238, 92, 66, "Tomato2"));
        colors.add(new ConsoleStr.RGB(205, 79, 57, "Tomato3"));
        colors.add(new ConsoleStr.RGB(139, 54, 38, "Tomato4"));
        colors.add(new ConsoleStr.RGB(255, 69, 0, "OrangeRed1"));
        colors.add(new ConsoleStr.RGB(238, 64, 0, "OrangeRed2"));
        colors.add(new ConsoleStr.RGB(205, 55, 0, "OrangeRed3"));
        colors.add(new ConsoleStr.RGB(139, 37, 0, "OrangeRed4"));
        colors.add(new ConsoleStr.RGB(255, 0, 0, "Red1"));
        colors.add(new ConsoleStr.RGB(238, 0, 0, "Red2"));
        colors.add(new ConsoleStr.RGB(205, 0, 0, "Red3"));
        colors.add(new ConsoleStr.RGB(139, 0, 0, "Red4"));
        colors.add(new ConsoleStr.RGB(255, 20, 147, "DeepPink1"));
        colors.add(new ConsoleStr.RGB(238, 18, 137, "DeepPink2"));
        colors.add(new ConsoleStr.RGB(205, 16, 118, "DeepPink3"));
        colors.add(new ConsoleStr.RGB(139, 10, 80, "DeepPink4"));
        colors.add(new ConsoleStr.RGB(255, 110, 180, "HotPink1"));
        colors.add(new ConsoleStr.RGB(238, 106, 167, "HotPink2"));
        colors.add(new ConsoleStr.RGB(205, 96, 144, "HotPink3"));
        colors.add(new ConsoleStr.RGB(139, 58, 98, "HotPink4"));
        colors.add(new ConsoleStr.RGB(255, 181, 197, "Pink1"));
        colors.add(new ConsoleStr.RGB(238, 169, 184, "Pink2"));
        colors.add(new ConsoleStr.RGB(205, 145, 158, "Pink3"));
        colors.add(new ConsoleStr.RGB(139, 99, 108, "Pink4"));
        colors.add(new ConsoleStr.RGB(255, 174, 185, "LightPink1"));
        colors.add(new ConsoleStr.RGB(139, 95, 101, "LightPink4"));
        colors.add(new ConsoleStr.RGB(255, 130, 171, "PaleVioletRed1"));
        colors.add(new ConsoleStr.RGB(238, 121, 159, "PaleVioletRed2"));
        colors.add(new ConsoleStr.RGB(205, 104, 137, "PaleVioletRed3"));
        colors.add(new ConsoleStr.RGB(139, 71, 93, "PaleVioletRed4"));
        colors.add(new ConsoleStr.RGB(255, 62, 150, "VioletRed1"));
        colors.add(new ConsoleStr.RGB(238, 58, 140, "VioletRed2"));
        colors.add(new ConsoleStr.RGB(205, 50, 120, "VioletRed3"));
        colors.add(new ConsoleStr.RGB(139, 34, 82, "VioletRed4"));
        colors.add(new ConsoleStr.RGB(255, 0, 255, "Magenta1"));
        colors.add(new ConsoleStr.RGB(238, 0, 238, "Magenta2"));
        colors.add(new ConsoleStr.RGB(205, 0, 205, "Magenta3"));
        colors.add(new ConsoleStr.RGB(139, 0, 139, "Magenta4"));
        colors.add(new ConsoleStr.RGB(255, 131, 250, "Orchid1"));
        colors.add(new ConsoleStr.RGB(238, 122, 233, "Orchid2"));
        colors.add(new ConsoleStr.RGB(205, 105, 201, "Orchid3"));
        colors.add(new ConsoleStr.RGB(139, 71, 137, "Orchid4"));
        colors.add(new ConsoleStr.RGB(255, 187, 255, "Plum1"));
        colors.add(new ConsoleStr.RGB(238, 174, 238, "Plum2"));
        colors.add(new ConsoleStr.RGB(205, 150, 205, "Plum3"));
        colors.add(new ConsoleStr.RGB(139, 102, 139, "Plum4"));
        colors.add(new ConsoleStr.RGB(191, 62, 255, "DarkOrchid1"));
        colors.add(new ConsoleStr.RGB(104, 34, 139, "DarkOrchid4"));
        colors.add(new ConsoleStr.RGB(155, 48, 255, "Purple1"));
        colors.add(new ConsoleStr.RGB(145, 44, 238, "Purple2"));
        colors.add(new ConsoleStr.RGB(125, 38, 205, "Purple3"));
        colors.add(new ConsoleStr.RGB(85, 26, 139, "Purple4"));
        colors.add(new ConsoleStr.RGB(171, 130, 255, "MediumPurple1"));
        colors.add(new ConsoleStr.RGB(159, 121, 238, "MediumPurple2"));
        colors.add(new ConsoleStr.RGB(137, 104, 205, "MediumPurple3"));
        colors.add(new ConsoleStr.RGB(93, 71, 139, "MediumPurple4"));
        colors.add(new ConsoleStr.RGB(255, 225, 255, "Thistle1"));
        colors.add(new ConsoleStr.RGB(238, 210, 238, "Thistle2"));
        colors.add(new ConsoleStr.RGB(205, 181, 205, "Thistle3"));
        colors.add(new ConsoleStr.RGB(139, 123, 139, "Thistle4"));
        colors.add(new ConsoleStr.RGB(0, 0, 139, "DarkBlue"));
        colors.add(new ConsoleStr.RGB(0, 139, 139, "DarkCyan"));
        colors.add(new ConsoleStr.RGB(139, 0, 139, "DarkMagenta"));
        colors.add(new ConsoleStr.RGB(139, 0, 0, "DarkRed"));
        colors.add(new ConsoleStr.RGB(144, 238, 144, "LightGreen"));
    }

    // --- format
    public LogMe setLogFormat(LogFormat logFormat) {
        if (logFormat == null) {
            throw new IllegalArgumentException("logFormat must not be null");
        }
        this.logFormat = logFormat;
        return this;
    }

    public int getSubjectId() {
        return this.subjectId++;
    }
}









