package com.afan.tool.string;

import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {

	public static final String NONE = "";
	public static final String COMMA = ",";
	public static final String SEMICOLON = ";";
	public static final String WELL = "#";
	public static final String AT = "@";
	public static final String STAR = "*";
	public static final String UNDERLINE = "_";
	public static final String MIDDLELINE = "-";

	public static final String UNKNOWN = "unknown";
	public static final String TRUE = "true";
	public static final String SUCCESS = "success";
	
	private static final String num = "0123456789";
	private static final String str = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
	private static final String baseStr = str+num;

	private static final Pattern mobilePattern = Pattern.compile("^(1[34578]\\d{9})$");
	private static final Pattern numberPattern = Pattern.compile("[0-9]*([.]([0-9]+))?");
	private static final Pattern mailPattern = Pattern.compile("[a-zA-Z_0-9]{1,}[0-9]{0,}@(([a-zA-z0-9]-*){1,}\\.){1,3}[a-zA-z\\-]{1,}");

	public static void main(String[] args) {
		System.out.println(isNumber("3."));
		System.out.println(mix("15968185312", 3, 4, 4));
		System.out.println(mixMobile("15968185312"));
		System.out.println(mixEmail("ccc@qq.com"));
		System.out.println(mixEmail("215853693@qq.cn"));
		System.out.println(unicodeEncode("陈家辉2010"));
	}

	public static int getInt(String s) {
		if (isNumber(s)) {
			try {
				return Integer.parseInt(s);
			} catch (Exception e) {
			}
		}
		return 0;
	}

	public static double getDouble(String s) {
		try {
			return Double.parseDouble(s);
		} catch (Exception e) {
		}
		return 0;
	}

	public static boolean isEmpty(String s) {
		return s == null || s.length() == 0;
	}

	public static boolean areBlank(String... s){
		for (String str : s) {
			if(!isBlank(str)){
				return false;
			}
		}
		return true;
	}
	
	public static boolean isBlank(String s) {
		int strLen;
		if (s == null || (strLen = s.length()) == 0) {
			return true;
		}
		for (int i = 0; i < strLen; i++) {
			if ((Character.isWhitespace(s.charAt(i)) == false)) {
				return false;
			}
		}
		return true;
	}

	public static boolean isNull(String s) {
		return s == null;
	}

	public static boolean isNumber(String s) {
		if (isBlank(s)) {
			return false;
		}
		return numberPattern.matcher(s).matches();
	}
	
	/**
	 * 判断是否全大于0
	 * @param nums
	 * @return
	 */
	public static boolean arePlus(int... nums){
		for (int i : nums) {
			if(i<=0){
				return false;
			}
		}
		return true;
	}
	
	public static boolean arePlus(long... nums){
		for (long i : nums) {
			if(i<=0){
				return false;
			}
		}
		return true;
	}

	public static boolean isTrue(int s) {
		return 0 == s;
	}

	public static boolean isTrue(String s) {
		if (isBlank(s)) {
			return false;
		}
		return (TRUE.equals(s) || SUCCESS.equals(s) || "1".equals(s));
	}

	public static boolean isMobilePhone(String s) {
		if (isBlank(s)) {
			return false;
		}
		return mobilePattern.matcher(s).find();
	}

	public static boolean isEmailAddress(String s) {
		if (isBlank(s)) {
			return false;
		}
		return mailPattern.matcher(s).find();
	}

	public static String clean(String s) {
		return s == null ? NONE : s.trim();
	}

	public static String trim(String s) {
		return s == null ? null : s.trim();
	}
	
	public static String append(Object...ss) {
		if(ss==null){
			return null;
		}
		StringBuilder str = new StringBuilder();
		for (Object s : ss) {
			str.append(s);
		}
		return str.toString();
	}

	/**
	 * 在source中查找target出现的次数
	 * 
	 * @param source
	 * @param target
	 * @return
	 */
	public static int find(String s, String t) {
		int number = 0;
		int i = 0;
		while ((i = s.indexOf(t, i)) != -1) {
			number++;
			i++;
		}
		return number;
	}

	public static String getUrlParamValue(String url, String name) {
		Pattern p = Pattern.compile(name + "=([^&]*)(&|$)");
		Matcher m = p.matcher(url);
		if (m.find()) {
			return m.group(1);
		}
		return null;
	}

	// 陈家辉2010==>\u9648\u5bb6\u8f892010
	public static String unicodeEncode(String chinaStr) {
		return unicodeEncode(chinaStr, "\\u");
	}

	public static String unicodeEncode(String chinaStr, String s) {
		StringBuffer unicode = new StringBuffer();
		for (int i = 0; i < chinaStr.length(); i++) {
			char c = chinaStr.charAt(i);
			String regEx = "[\\u4e00-\\u9fa5]";
			Pattern p = Pattern.compile(regEx);
			Matcher m = p.matcher("" + c);
			if (m.find()) {
				unicode.append(s + Integer.toHexString(c));
				continue;
			}
			unicode.append(c);
		}
		return unicode.toString();
	}

	// \u9648\u5bb6\u8f892010==>陈家辉2010
	public static String unicodeDecode(String unicodeStr) {
		char aChar;
		int len = unicodeStr.length();
		StringBuffer outBuffer = new StringBuffer(len);
		for (int x = 0; x < len;) {
			aChar = unicodeStr.charAt(x++);
			if (aChar == '\\') {
				aChar = unicodeStr.charAt(x++);
				if (aChar == 'u') {
					// Read the xxxx
					int value = 0;
					for (int i = 0; i < 4; i++) {
						aChar = unicodeStr.charAt(x++);
						switch (aChar) {
						case '0':
						case '1':
						case '2':
						case '3':
						case '4':
						case '5':
						case '6':
						case '7':
						case '8':
						case '9':
							value = (value << 4) + aChar - '0';
							break;
						case 'a':
						case 'b':
						case 'c':
						case 'd':
						case 'e':
						case 'f':
							value = (value << 4) + 10 + aChar - 'a';
							break;
						case 'A':
						case 'B':
						case 'C':
						case 'D':
						case 'E':
						case 'F':
							value = (value << 4) + 10 + aChar - 'A';
							break;
						default:
							throw new IllegalArgumentException("Malformed   \\uxxxx   encoding.");
						}

					}
					outBuffer.append((char) value);
				} else {
					if (aChar == 't')
						aChar = '\t';
					else if (aChar == 'r')
						aChar = '\r';
					else if (aChar == 'n')
						aChar = '\n';
					else if (aChar == 'f')
						aChar = '\f';
					outBuffer.append(aChar);
				}
			} else
				outBuffer.append(aChar);
		}
		return outBuffer.toString();
	}
	
	public static String mix(String s, int length) {
		if (isEmpty(s)) {
			return null;
		}
		if (s.length() <= length) {
			length = s.length() - 1;
		}
		int start = length / 2;
		int end = length - start;
		int startCount = 3;
		return mix(s, start, end, startCount);
	}

	public static String mix(String s, int start, int end, int starCount) {
		StringBuilder mix = new StringBuilder();
		mix.append(s.substring(0, start));
		for (int i = 0; i < starCount; i++) {
			mix.append(STAR);
		}
		mix.append(s.substring(s.length() - end, s.length()));
		return mix.toString();
	}

	public static String mixMobile(String s) {
		if (isMobilePhone(s)) {
			return mix(s, 3, 4, 4);
		}
		return s;
	}

	public static String mixEmail(String s) {
		if (isEmailAddress(s)) {
			StringBuilder mix = new StringBuilder();
			String mailAccount = s.substring(0, s.indexOf(AT));
			if (mailAccount.length() > 3) {
				mix.append(mailAccount.substring(0, 3));
				mix.append(STAR).append(STAR).append(STAR);
			} else {
				mix.append(mailAccount.substring(0, 1));
				mix.append(STAR).append(STAR).append(STAR);
			}
			mix.append(s.substring(s.indexOf(AT)));
			return mix.toString();
		}
		return s;
	}
	
	public static String getRandomNum(int length) {
		Random random = new Random();     
	    StringBuffer sb = new StringBuffer();     
	    for (int i = 0; i < length; i++) {     
	        int number = random.nextInt(num.length());     
	        sb.append(num.charAt(number));     
	    }     
	    return sb.toString();
	}
	
	public static String getRandomStr(int length) {
		Random random = new Random();     
	    StringBuffer sb = new StringBuffer();     
	    for (int i = 0; i < length; i++) {     
	        int number = random.nextInt(baseStr.length());     
	        sb.append(baseStr.charAt(number));     
	    }     
	    return sb.toString();
	}

}
