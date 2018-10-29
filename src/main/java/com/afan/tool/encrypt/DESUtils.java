package com.afan.tool.encrypt;

import java.security.SecureRandom;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

import com.afan.tool.string.StringUtil;

/**
 * 对称加密
 * 
 * @author afan
 * 
 */
public class DESUtils {
	
	private static final String DES = "DES";
	private static String CHARTSET = "utf-8";
	private static final String DEFAULT_KEY = "023rEAiL15RgD51HOMkL1aBpiL1rEAi5";
	
	public static String encrypt(String content) {
		if (!StringUtil.isBlank(content)) {
			return encrypt(content, DEFAULT_KEY);
		}
		return null;
	}

	public static String decrypt(String content) {
		if (!StringUtil.isBlank(content)) {
			return decrypt(content, DEFAULT_KEY);
		}
		return null;
	}

	/**
	 * 加密
	 * @param content
	 * @param secretKey
	 * @return
	 */
	public static String encrypt(String content, String secretKey) {
		try {
			SecureRandom random = new SecureRandom();
			DESKeySpec desKey = new DESKeySpec(secretKey.getBytes());
			// 创建一个密匙工厂，然后用它把DESKeySpec转换成
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES);
			SecretKey securekey = keyFactory.generateSecret(desKey);
			// Cipher对象实际完成加密操作
			Cipher cipher = Cipher.getInstance(DES);
			// 用密匙初始化Cipher对象
			cipher.init(Cipher.ENCRYPT_MODE, securekey, random);
			// 现在，获取数据并加密
			// 正式执行加密操作
			byte[] encrypt = cipher.doFinal(content.getBytes(CHARTSET));
			return ByteUtils.byteArray2Base64(encrypt);
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 解密
	 * @param content
	 * @param secretKey
	 * @return
	 * @throws Exception
	 */
	public static String decrypt(String content, String secretKey) {
		try {
			// DES算法要求有一个可信任的随机数源
			SecureRandom random = new SecureRandom();
			// 创建一个DESKeySpec对象
			DESKeySpec desKey = new DESKeySpec(secretKey.getBytes());
			// 创建一个密匙工厂
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES);
			// 将DESKeySpec对象转换成SecretKey对象
			SecretKey securekey = keyFactory.generateSecret(desKey);
			// Cipher对象实际完成解密操作
			Cipher cipher = Cipher.getInstance(DES);
			// 用密匙初始化Cipher对象
			cipher.init(Cipher.DECRYPT_MODE, securekey, random);
			// 真正开始解密操作
			byte[] decrypt = cipher.doFinal(ByteUtils.base642byteArray(content));
			return new String(decrypt, CHARTSET);
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void main(String[] args) {
		String encodeRules = "023rEAiL15RgD51HOMkL1aBpiL1rEAi5";
		String content = "159ALSS5312";
		String encrypt = encrypt(content, encodeRules);
		String decrypt = decrypt(encrypt, encodeRules);
		System.out.println("根据输入的规则" + encodeRules + "加密后的密文是:" + encrypt);
		System.out.println("使用DES对称解密，请输入加密的规则：(须与加密相同)");
		System.out.println("请输入要解密的内容（密文）:" + encrypt);
		System.out.println("根据输入的规则" + encodeRules + "解密后的明文是:" + decrypt);
	}
}
