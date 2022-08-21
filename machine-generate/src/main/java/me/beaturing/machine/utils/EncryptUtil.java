package me.beaturing.machine.utils;

import org.apache.commons.codec.binary.Base64;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class EncryptUtil {

	/**
	 * md5加密
	 *@param text
	 * @return
	 */
	public final static String encodeMD5(String text){
		StringBuilder sb = new StringBuilder();
		try {
			MessageDigest messageDigest = MessageDigest.getInstance("MD5");
			byte[] digest = messageDigest.digest(text.getBytes());
			for (int i = 0; i < digest.length; i++) {
				int bt = digest[i]&0xff;
				if (bt < 16) {
					sb.append(0);
				}
				sb.append(Integer.toHexString(bt));
			}
		} catch (NoSuchAlgorithmException e) {
			return null;
		}
		return sb.toString().toUpperCase();
	}

	/**
	 * base64加密
	 * @param binaryData
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public final static String encodeBase64(byte[] binaryData) throws UnsupportedEncodingException {
		return new String(Base64.encodeBase64(binaryData), "UTF-8");
	}

	/**
	 * base64解密
	 * @param binaryData
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public final static String decodeBase64(byte[] binaryData) throws UnsupportedEncodingException {
		return new String(Base64.decodeBase64(binaryData), "UTF-8");
	}

}
