package com.afan.tool.encrypt;

import org.apache.commons.codec.binary.Base64;

public class ByteUtils {

	/**
	 * 将byte数组转换为表示16进制值的字符串
	 * 
	 * @param arrB
	 * @return
	 * @throws Exception
	 */
	public static String byteArray2HexStr(byte[] arr) throws Exception {
		int iLen = arr.length;
		// 每个byte用两个字符才能表示，所以字符串的长度是数组长度的两倍
		StringBuilder sb = new StringBuilder(iLen * 2);
		for (int i = 0; i < iLen; i++) {
			int intTmp = arr[i];
			// 把负数转换为正数
			while (intTmp < 0) {
				intTmp = intTmp + 256;
			}
			// 小于0F的数需要在前面补0
			if (intTmp < 16) {
				sb.append("0");
			}
			sb.append(Integer.toString(intTmp, 16));
		}
		return sb.toString();
	}

	/**
	 * 将表示16进制值的字符串转换为byte数组
	 * 
	 * @param strIn
	 * @return
	 * @throws Exception
	 */
	public static byte[] hexStr2ByteArray(String str) throws Exception {
		byte[] arrB = str.getBytes();
		int iLen = arrB.length;
		// 两个字符表示一个字节，所以字节数组长度是字符串长度除以2
		byte[] arrOut = new byte[iLen / 2];
		for (int i = 0; i < iLen; i = i + 2) {
			String strTmp = new String(arrB, i, 2);
			arrOut[i / 2] = (byte) Integer.parseInt(strTmp, 16);
		}
		return arrOut;
	}

	/**
	 * 将byte数组转换为base64字符串
	 * 
	 * @param arr
	 * @return
	 * @throws Exception
	 */
	public static String byteArray2Base64(byte[] arr) throws Exception {
		return new String(Base64.encodeBase64(arr));
	}

	/**
	 * 将base64字符串转换为byte数组
	 * 
	 * @param str
	 * @return
	 * @throws Exception
	 */
	public static byte[] base642byteArray(String str) throws Exception {
		return Base64.decodeBase64(str);
	}

}
