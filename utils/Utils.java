package utils;

public class Utils {

	public static void stringToByteArray(String s, byte[] b) {
		int i = 0;
		for (char c : s.toCharArray()) {
			b[i] = (byte)c;
			i++;
		}
	}
}
