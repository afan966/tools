package com.afan.tool.string;

import java.math.BigInteger;
import java.util.Stack;

/**
 * 10转换成N进制
 * 
 * @author cf
 *
 */
public class BigintConvert {

	private String keys = "QWERTYUIOPASDFGHJKLZXCVBNM";
	private char[] array = null;
	private int ONEMAX = 0;
	private final char ZERO = '0';
	
	public BigintConvert(String keys) throws Exception {
		this.keys = keys;
		char[] seedKeys = keys.toCharArray();
		StringBuilder seed = new StringBuilder();
		for (int i = 0; i < seedKeys.length; i++) {
			char s = seedKeys[i];
			if(seed.indexOf(String.valueOf(s))==-1){
				seed.append(s);
			}
		}
		array = seed.toString().toCharArray();
		ONEMAX = seed.length();
		if(ONEMAX<2){
			throw new Exception("keys length < 2");
		}
	}
	
	public BigintConvert(){
		init();
	}
	
	private void init(){
		array = keys.toCharArray();
		ONEMAX = keys.length();
	}
	
	public String encode(long number) {
		return _10_to_bigint(BigInteger.valueOf(number));
	}
	
	public long decode(String number) {
		return _bigint_to_10(number).longValue();
	}

	private String _10_to_bigint(BigInteger number) {
		BigInteger rest = number;
		BigInteger max = BigInteger.valueOf(ONEMAX);
		Stack<Character> stack = new Stack<Character>();
		StringBuilder result = new StringBuilder(0);
		//number>0
		while (rest.compareTo(BigInteger.valueOf(0)) == 1) {
			stack.add(array[rest.subtract(rest.divide(max).multiply(max)).intValue()]);
			rest = rest.divide(max);
		}
		for (; !stack.isEmpty();) {
			result.append(stack.pop());
		}
		return result.toString();

	}
	
	private BigInteger _bigint_to_10(String sixty_str) {
		BigInteger multiple = BigInteger.valueOf(1);
		BigInteger result = BigInteger.valueOf(0);
		Character c;
		for (int i = 0; i < sixty_str.length(); i++) {
			c = sixty_str.charAt(sixty_str.length() - i - 1);
			//System.out.println(c+"  "+_62_value(c)+"  "+multiple);
			result = result.add(multiple.multiply(BigInteger.valueOf(_bigint_value(c))));
			multiple = multiple.multiply(BigInteger.valueOf(ONEMAX));
		}
		return result;
	}

	private int _bigint_value(Character c) {
		for (int i = 0; i < array.length; i++) {
			if (c == array[i]) {
				return i;
			}
		}
		return 0;
	}
	
	//隔位对称反转 1234567890==>0284567391
	public String security(String code) {
		char[] temp = code.toCharArray();
		int size = temp.length;
		char[] result = new char[size];
		for (int i = 0; i <= temp.length / 2; i++) {
			if (i % 2 == 0) {
				result[i] = temp[size - 1 - i];
				result[size - 1 - i] = temp[i];
			} else {
				result[i] = temp[i];
				result[size - 1 - i] = temp[size - 1 - i];
			}
		}
		return new String(result);
	}
	
	//隔位对称反转 1234567890==>9274563810最后一位是0保持原位置
	public BigInteger security(BigInteger code) {
		char[] temp = code.toString().toCharArray();
		char[] _temp = null;
		char[] result = null;
		// 最后一位是0就不反转
		String offset = new String();
		if (temp[temp.length - 1] == ZERO) {
			_temp = code.toString().substring(0, temp.length - 1).toCharArray();
			result = new char[temp.length - 1];
			offset = String.valueOf(ZERO);
		} else {
			_temp = temp;
			result = new char[temp.length];
		}
		int size = _temp.length;
		for (int i = 0; i <= _temp.length / 2; i++) {
			if (i % 2 == 0) {
				result[i] = _temp[size - 1 - i];
				result[size - 1 - i] = _temp[i];
			} else {
				result[i] = _temp[i];
				result[size - 1 - i] = _temp[size - 1 - i];
			}
		}
		return new BigInteger(new String(result) + offset);
	}
	
	public static void main(String[] args) {
		BigintConvert convert = null;
		try {
			convert = new BigintConvert("qaswsmn");
			System.out.println(convert._bigint_to_10("mmmm").toString());
			System.out.println(convert.encode(9999));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
