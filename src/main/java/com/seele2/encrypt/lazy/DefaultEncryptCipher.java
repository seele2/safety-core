package com.seele2.encrypt.lazy;

import com.seele2.encrypt.base.EncryptCipher;
import com.seele2.encrypt.tool.Base64Helper;
import com.seele2.encrypt.tool.StrHelper;

import java.util.List;

public class DefaultEncryptCipher implements EncryptCipher {

	private static final String targetStr = "==";

	private static final String replaceStr = "$";

	private static final int step = 4;

	@Override
	public String encrypt(Object s) {
		List<String>  str = StrHelper.breakup(transToString(s));
		StringBuilder sb  = new StringBuilder();
		str.forEach(i -> sb.append(Base64Helper.encode(i)));
		return sb.toString().replace(targetStr, replaceStr);
	}

	@Override
	public String decrypt(Object s) {
		String        str = transToString(s).replace(replaceStr, targetStr);
		int           len = str.length();
		int           c   = (int) Math.ceil((double) len / step);
		StringBuilder sb  = new StringBuilder();
		for (int i = 0; i < c; i++) {
			sb.append(new String(Base64Helper.decode(str.substring(i * step, Math.min(i * step + step, len)))));
		}
		return sb.toString();
	}

	private String transToString(Object s) {
		if (s instanceof String) {
			return (String) s;
		}
		return String.valueOf(s);
	}


}
