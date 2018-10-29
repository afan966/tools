package com.afan.tool.encrypt;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import javax.crypto.Cipher;
import org.apache.commons.net.util.Base64;

/**
 * 非对称加密，公钥加密，私钥解密
 * 
 * @author afan
 * 
 */
public class RSAUtils {

	private static KeyPair keyPair = genKeyPair(600);

	// 生成密钥对
	public static KeyPair genKeyPair(int keyLength) {
		try {
			KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
			keyPairGenerator.initialize(keyLength);
			return keyPairGenerator.generateKeyPair();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String getPublicKey() {
		// 获取公钥，并以base64格式打印出来
		PublicKey publicKey = keyPair.getPublic();
		return new String(Base64.encodeBase64(publicKey.getEncoded()));
	}

	public static String getPrivateKey() {
		// 获取私钥，并以base64格式打印出来
		PrivateKey privateKey = keyPair.getPrivate();
		return new String(Base64.encodeBase64(privateKey.getEncoded()));
	}

	// 将base64编码后的公钥字符串转成PublicKey实例
	public static PublicKey getPublicKey(String publicKey) throws Exception {
		byte[] keyBytes = Base64.decodeBase64(publicKey.getBytes());
		X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		return keyFactory.generatePublic(keySpec);
	}

	// 将base64编码后的私钥字符串转成PrivateKey实例
	public static PrivateKey getPrivateKey(String privateKey) throws Exception {
		byte[] keyBytes = Base64.decodeBase64(privateKey.getBytes());
		PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		return keyFactory.generatePrivate(keySpec);
	}

	// 公钥加密
	public static byte[] encrypt(byte[] content, PublicKey publicKey) throws Exception {
		Cipher cipher = Cipher.getInstance("RSA");// java默认"RSA"="RSA/ECB/PKCS1Padding"
		cipher.init(Cipher.ENCRYPT_MODE, publicKey);
		return cipher.doFinal(content);
	}

	// 私钥解密
	public static byte[] decrypt(byte[] content, PrivateKey privateKey) throws Exception {
		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.DECRYPT_MODE, privateKey);
		return cipher.doFinal(content);
	}

	public static void main(String[] args) {
		try {
			
			//keyPair = genKeyPair(128);
			String publicKey = getPublicKey();
			String privateKey = getPrivateKey();
			PublicKey pubKey = getPublicKey(publicKey);
			PrivateKey priKey = getPrivateKey(privateKey);

			System.out.println(publicKey);
			System.out.println(privateKey);

			String data = "15968185312";
			byte[] enc = encrypt(data.getBytes(), pubKey);

			System.out.println(enc.length + "  " + new String(enc, "utf-8"));
			byte[] res = decrypt(enc, priKey);
			System.out.println(new String(res));
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
