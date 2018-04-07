package com.afan.tool.encrypt;

import java.security.Key;
import java.security.Security;

import javax.crypto.Cipher;

/**
 * 对称加密
 * @author afan
 *
 */
public class DESUtils {

	/** 字符串默认键值 */
	private static String strDefaultKey = "019a5b3538f0d1dea53737e6d32de5ab";

	/** 加密工具 */
	private Cipher encryptCipher = null;

	/** 解密工具 */
	private Cipher decryptCipher = null;

	/**
	 * 将byte数组转换为表示16进制值的字符串
	 * @param arrB
	 * @return
	 * @throws Exception
	 */
	private static String byteArr2HexStr(byte[] arrB) throws Exception {
		int iLen = arrB.length;
		// 每个byte用两个字符才能表示，所以字符串的长度是数组长度的两倍
		StringBuffer sb = new StringBuffer(iLen * 2);
		for (int i = 0; i < iLen; i++) {
			int intTmp = arrB[i];
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
	 * @param strIn
	 * @return
	 * @throws Exception
	 */
	private static byte[] hexStr2ByteArr(String strIn) throws Exception {
		byte[] arrB = strIn.getBytes();
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
	 * 默认构造方法，使用默认密钥
	 * 
	 * @throws Exception
	 */
	public DESUtils() throws Exception {
		this(strDefaultKey);
	}

	/**
	 * 指定密钥构造方法
	 * 
	 * @param strKey
	 *            指定的密钥
	 * @throws Exception
	 */
	public DESUtils(String strKey) throws Exception {
		Security.addProvider(new com.sun.crypto.provider.SunJCE());
		Key key = getKey(strKey.getBytes());

		encryptCipher = Cipher.getInstance("DES");
		encryptCipher.init(Cipher.ENCRYPT_MODE, key);

		decryptCipher = Cipher.getInstance("DES");
		decryptCipher.init(Cipher.DECRYPT_MODE, key);
	}

	/**
	 * 加密字节数组
	 * @param byteArr
	 * @return
	 * @throws Exception
	 */
	public byte[] encrypt(byte[] byteArr) throws Exception {
		return encryptCipher.doFinal(byteArr);
	}

	/**
	 * 加密字符串
	 * @param str
	 * @return
	 * @throws Exception
	 */
	public String encrypt(String str) throws Exception {
		return byteArr2HexStr(encrypt(str.getBytes()));
	}

	/**
	 * 解密字节数组
	 * @param byteArr
	 * @return
	 * @throws Exception
	 */
	public byte[] decrypt(byte[] byteArr) throws Exception {
		return decryptCipher.doFinal(byteArr);
	}

	/**
	 * 解密字符串
	 * @param strIn
	 * @return
	 * @throws Exception
	 */
	public String decrypt(String str) throws Exception {
		return new String(decrypt(hexStr2ByteArr(str)));
	}

	/**
	 * 从指定字符串生成密钥，密钥所需的字节数组长度为8位 不足8位时后面补0，超出8位只取前8位
	 * @param byteArr
	 * @return
	 * @throws Exception
	 */
	private Key getKey(byte[] byteArr) throws Exception {
		// 创建一个空的8位字节数组（默认值为0）
		byte[] array = new byte[8];
		// 将原始字节数组转换为8位
		for (int i = 0; i < byteArr.length && i < array.length; i++) {
			array[i] = byteArr[i];
		}
		// 生成密钥
		Key key = new javax.crypto.spec.SecretKeySpec(array, "DES");
		return key;
	}

	public static void main(String[] args) {
		try {
			String test = "15968185312";
			DESUtils des = new DESUtils("slkdd");// 自定义密钥
			System.out.println("加密前的字符：" + test);
			System.out.println("加密后的字符：" + des.encrypt(test));
			System.out.println("解密后的字符：" + des.decrypt(des.encrypt(test)));
			System.out.println("解密后的字符：" + des.decrypt("87078bdee9b5d510f63cce896853fa95"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
