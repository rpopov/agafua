package com.agafua.syslog.util;

/**
 * Validation utilities.
 * 
 */
public class Validator {

	private Validator() {
	};

	public static String replaceNonUsAsciiAndTrim(String s, int maxLength) {
		s = s.substring(0, Math.min(s.length(), maxLength));

		// Only ASCII-7 chars are allowed. So replace all others:
		return s.replaceAll("[\\x80-\\xFF]", ".");
	}
}
