package com.afan.tool.encrypt;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * SHA加密工具
 * 
 * @author afan
 * 
 */
public class SHAUtils {
	
	public static final String SHA_256 = "SHA-256";
	public static final String SHA_512 = "SHA-512";

	/**
	 * 传入文本内容，返回 SHA-256 串
	 * 
	 * @param strText
	 * @return
	 */
	public static String sha256(final String strText) {
		return sha(strText, SHA_256);
	}

	/**
	 * 传入文本内容，返回 SHA-512 串
	 * 
	 * @param strText
	 * @return
	 */
	public static String sha512(final String strText) {
		return sha(strText, SHA_512);
	}

	/**
	 * 字符串 SHA 加密
	 * 
	 * @param strSourceText
	 * @return
	 */
	private static String sha(final String strText, final String strType) {
		// 返回值
		String strResult = null;
		// 是否是有效字符串
		if (strText != null && strText.length() > 0) {
			try {
				// 创建加密对象
				MessageDigest messageDigest = MessageDigest.getInstance(strType);
				// 传入要加密的字符串
				messageDigest.update(strText.getBytes());
				// 得到 byte
				byte byteBuffer[] = messageDigest.digest();
				// 转 string
				StringBuffer strHexString = new StringBuffer();
				for (int i = 0; i < byteBuffer.length; i++) {
					String hex = Integer.toHexString(0xff & byteBuffer[i]);
					if (hex.length() == 1) {
						strHexString.append('0');
					}
					strHexString.append(hex);
				}
				// 得到返回結果
				strResult = strHexString.toString();
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			}
		}

		return strResult;
	}

}
