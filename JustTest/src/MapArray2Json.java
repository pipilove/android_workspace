import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapArray2Json {
	public static void main(String[] args) {
		// [{"memberName":"卡卡" , "memberID":"330"},{"memberName" : "卡卡微" ,
		// "memberID":"702"}]
		String a = "[{\"memberName\":\"卡卡\" ,   \"memberID\":\"330\"},{\"memberName\"    : \"卡卡微\" , \"memberID\":\"702\"}]";
		List<Object> la = StringToMapList(a);
		for (Object C : la)
			System.out.println(C);
	}

	/**
	 * String转换Map
	 * 
	 * @param mapText
	 *            :需要转换的字符串
	 * @param KeySeparator
	 *            :字符串中的分隔符每一个key与value中的分割
	 * @param ElementSeparator
	 *            :字符串中每个元素的分割
	 * @return Map<?,?>
	 */
	public static Map<String, Object> StringToMap(String mapText) {

		if (mapText == null || mapText.equals("")) {
			return null;
		}
		// mapText = mapText.substring(1);

		Map<String, Object> map = new HashMap<String, Object>();
		String[] text = mapText.split(","); // 转换为数组
		for (String str : text) {
			String[] keyText = str.split(":"); // 转换key与value的数组
			if (keyText.length < 1) {
				continue;
			}
			String key = keyText[0].split("\"")[1]; // key
			String value = keyText[1].split("\"")[1]; // value
			// System.out.println("***" + key + "****");
			// System.out.println("***" + value + "****");
			map.put(key, value);
		}
		return map;
	}

	/**
	 * String转换List
	 * 
	 * @param listText
	 *            :需要转换的文本
	 * @return List<?>
	 */
	public static List<Object> StringToMapList(String listText) {
		if (listText == null || listText.equals("")) {
			return null;
		}
		listText = listText.substring(1, listText.length() - 1);
		// System.out.println(listText);

		List<Object> list = new ArrayList<Object>();
		String[] text = listText.trim().split("\\},\\{|\\{|\\}");
		for (String str : text) {
			if (str.length() >= 1) {
				// System.out.println("***" + str);
				Map<?, ?> map = StringToMap(str);
				list.add(map);
			}
		}
		return list;
	}
}
