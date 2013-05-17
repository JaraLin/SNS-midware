package com.sns.midware.utils;

public class Encrypt {

	private static int KRYPTON_SALT = 0xFF;

	public static String encrypt(String inStr) {
		String outStr = "";

		int krypton = 0;
		for (int idx = 0; idx < inStr.length(); idx++) {
			krypton = (int) inStr.charAt(idx);
			krypton ^= KRYPTON_SALT;
			String hex = Integer.toHexString(krypton);
			outStr += hex.length() < 2 ? "0" + hex : hex;
		}

		return outStr;
	}

	public static String decrypt(String inStr) {
		String outStr = "";

		try {
			for (int idx = 0; idx + 1 < inStr.length(); idx += 2) {
				String hexStr = inStr.substring(idx, idx + 2);
				int hex = Integer.parseInt(hexStr, 16) ^ KRYPTON_SALT;
				outStr += (char) hex;
			}
		} catch (Exception e) {
			return inStr;
		}

		return outStr;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String inStr = "jc8910";
		String outStr = Encrypt.encrypt(inStr);
		System.out.println("encrypted words:" + outStr);
		System.out
				.println("decrypted words:" + Encrypt.decrypt(outStr));
	}

}
