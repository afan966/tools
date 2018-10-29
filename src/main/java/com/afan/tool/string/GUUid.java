package com.afan.tool.string;

import java.util.Random;
import java.util.UUID;

public class GUUid {

	private static final String seedStr = "abcdefghijklmnopqrstuivwxyz0123456789ABCDEFGHIJKLMNOPQRSTUIVWXYZ";

	private static char[] chars = null;

	static {
		chars = seedStr.toCharArray();
		Random rnd = new Random();
		for (int i = chars.length; i > 1; i--) {
			swap(chars, i - 1, rnd.nextInt(i));
		}
	}

	private static void swap(char[] arr, int i, int j) {
		char tmp = arr[i];
		arr[i] = arr[j];
		arr[j] = tmp;
	}

	public static void main(String[] args) {
		for (int i=0;i<100;i++) {
			System.out.println(getUid());
			System.out.println(getShortUid());
		}
	}

	public static String getUid() {
		return UUID.randomUUID().toString().replace("-", "");
	}

	/**
	 * 生成一个8位的短ID,64的8次方个不同的ID
	 */
	public static String getShortUid() {
		StringBuilder sb = new StringBuilder();
		String uuid = getUid();
		for (int i = 0; i < 8; i++) {
			String str = uuid.substring(i * 4, i * 4 + 4);
			int x = Integer.parseInt(str, 16);
			sb.append(chars[x % 0x3E]);
		}
		return sb.toString();
	}
}
