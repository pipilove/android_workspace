import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapArray2Json {
	public static void main(String[] args) {
		// [{"memberName":"����" , "memberID":"330"},{"memberName" : "����΢" ,
		// "memberID":"702"}]
		String a = "[{\"memberName\":\"����\" ,   \"memberID\":\"330\"},{\"memberName\"    : \"����΢\" , \"memberID\":\"702\"}]";
		List<Object> la = StringToMapList(a);
		for (Object C : la)
			System.out.println(C);
	}

	/**
	 * Stringת��Map
	 * 
	 * @param mapText
	 *            :��Ҫת�����ַ���
	 * @param KeySeparator
	 *            :�ַ����еķָ���ÿһ��key��value�еķָ�
	 * @param ElementSeparator
	 *            :�ַ�����ÿ��Ԫ�صķָ�
	 * @return Map<?,?>
	 */
	public static Map<String, Object> StringToMap(String mapText) {

		if (mapText == null || mapText.equals("")) {
			return null;
		}
		// mapText = mapText.substring(1);

		Map<String, Object> map = new HashMap<String, Object>();
		String[] text = mapText.split(","); // ת��Ϊ����
		for (String str : text) {
			String[] keyText = str.split(":"); // ת��key��value������
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
	 * Stringת��List
	 * 
	 * @param listText
	 *            :��Ҫת�����ı�
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
