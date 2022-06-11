package red.medusa.logme.color;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author Mr.Medusa
 * @date 2022/6/7
 */
public class Emoji {
	private static final List<String> emojis = new ArrayList<>();
	private static final Random random = new Random();

	@Override
	public String toString() {
		return emojis.get(random.nextInt(emojis.size()));
	}

	static {
		emojis.add("😀");
		emojis.add("😁");
		emojis.add("😂");
		emojis.add("😃");
		emojis.add("😄");
		emojis.add("😅");
		emojis.add("😆");
		emojis.add("😇");
		emojis.add("😈");
		emojis.add("😉");
		emojis.add("😊");
		emojis.add("😋");
		emojis.add("😌");
		emojis.add("😍");
		emojis.add("😎");
		emojis.add("😏");
		emojis.add("😐");
		emojis.add("😑");
		emojis.add("😒");
		emojis.add("😓");
		emojis.add("😔");
		emojis.add("😕");
		emojis.add("😖");
		emojis.add("😗");
		emojis.add("😘");
		emojis.add("😙");
		emojis.add("😚");
		emojis.add("😛");
		emojis.add("😜");
		emojis.add("😝");
		emojis.add("😞");
		emojis.add("😟");
		emojis.add("😠");
		emojis.add("😡");
		emojis.add("😢");
		emojis.add("😣");
		emojis.add("😤");
		emojis.add("😥");
		emojis.add("😦");
		emojis.add("😧");
		emojis.add("😨");
		emojis.add("😩");
		emojis.add("😪");
		emojis.add("😫");
		emojis.add("😬");
		emojis.add("😭");
		emojis.add("😮");
		emojis.add("😯");
		emojis.add("😰");
		emojis.add("😱");
		emojis.add("😲");
		emojis.add("😳");
		emojis.add("😴");
		emojis.add("😵");
		emojis.add("😶");
		emojis.add("😷");
		emojis.add("🙁");
		emojis.add("🙂");
		emojis.add("🙃");
		emojis.add("🙄");
		emojis.add("🤐");
		emojis.add("🤑");
		emojis.add("🤒");
		emojis.add("🤓");
		emojis.add("🤔");
		emojis.add("🤕");
		emojis.add("🤠");
		emojis.add("🤡");
		emojis.add("🤢");
		emojis.add("🤣");
		emojis.add("🤤");
		emojis.add("🤥");
		emojis.add("🤧");
		emojis.add("🤨");
		emojis.add("🤩");
		emojis.add("🤪");
		emojis.add("🤫");
		emojis.add("🤬");
		emojis.add("🤭");
		emojis.add("🤮");
		emojis.add("🤯");
		emojis.add("🧐");
	}
}
