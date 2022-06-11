package red.medusa.logme.color;

import java.util.ArrayList;
import java.util.List;

public class NiceColor{
	public static List<ConsoleStr.RGB> colors = new ArrayList<>();
	static {
		colors.add(new ConsoleStr.RGB(255,110,180,"HotPink1"));		// HotPink1
		colors.add(new ConsoleStr.RGB(255, 20, 147));		// 深红色 very good

		colors.add(new ConsoleStr.RGB(238, 174, 238));		// 紫红色

	}
}