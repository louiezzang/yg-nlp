package com.yglab.nlp.util;

import java.lang.Character.UnicodeBlock;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

public class LangUtil {

	public static final boolean isAlphabet(char c) {
		if ((0x61 <= c && c <= 0x7A) || (0x41 <= c && c <= 0x5A))
			return true;
		
		return false;
	}
	
	public static final boolean isJapanese(char c) {
		if ((0x3040 <= c && c <= 0x309f) || (0x30a0 <= c && c <= 0x30ff) || (0x4e00 <= c && c <= 0x9faf))
			return true;
		
		return false;
	}
	
	public static final boolean isJapanese(String s) {
		for (int i = 0; i < s.length(); i++) {
			char ch = s.charAt(i);
			boolean isJapanese = isJapanese(ch);
			if (!isJapanese) {
				return false;
			}
		}
		
		return true;
	}
	
	@SuppressWarnings("serial")
	public static final boolean isChinese(char c) {
		Set<UnicodeBlock> chineseUnicodeBlocks = new HashSet<UnicodeBlock>() {{
	    add(UnicodeBlock.CJK_COMPATIBILITY);
	    add(UnicodeBlock.CJK_COMPATIBILITY_FORMS);
	    add(UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS);
	    add(UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS_SUPPLEMENT);
	    add(UnicodeBlock.CJK_RADICALS_SUPPLEMENT);
	    add(UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION);
	    add(UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS);
	    add(UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A);
	    add(UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B);
	    add(UnicodeBlock.KANGXI_RADICALS);
	    add(UnicodeBlock.IDEOGRAPHIC_DESCRIPTION_CHARACTERS);
		}};
		
		if (chineseUnicodeBlocks.contains(UnicodeBlock.of(c))) {
			return true;
		}
		
		return false;
	}
	
	public static final boolean isChinese(String s) {
		for (int i = 0; i < s.length(); i++) {
			char ch = s.charAt(i);
			boolean isChinese = isChinese(ch);
			if (!isChinese) {
				return false;
			}
		}
		
		return true;
	}
	
	public static final boolean isAlphabet(String s) {
		for (int i = 0; i < s.length(); i++) {
			char ch = s.charAt(i);
			boolean isAlphabet = isAlphabet(ch);
			if (!isAlphabet) {
				return false;
			}
		}
		
		return true;
	}
	
	public static final boolean containsAlphabet(String s) {
		for (int i = 0; i < s.length(); i++) {
			char ch = s.charAt(i);
			boolean isAlphabet = isAlphabet(ch);
			if (isAlphabet) {
				return true;
			}
		}
		
		return false;
	}
	
	public static final boolean isHangul(char c) {
		 if ((0xAC00 <= c && c <= 0xD7A3) || (0x3131 <= c && c <= 0x318E))
			return true;
		
		return false;
	}
	
	public static final boolean isHangul(String s) {
		for (int i = 0; i < s.length(); i++) {
			char ch = s.charAt(i);
			boolean isHangul = isHangul(ch);
			if (!isHangul) {
				return false;
			}
		}
		
		return true;
	}
	
	public static final boolean containsHangul(String s) {
		for (int i = 0; i < s.length(); i++) {
			char ch = s.charAt(i);
			boolean isHangul = isHangul(ch);
			if (isHangul) {
				return true;
			}
		}
		return false;
	}
	
	public static final boolean isNumeric(char c) {
		 if (0x30 <= c && c <= 0x39)
			return true;
		
		return false;
	}
	
	public static boolean isNumeric(String s) { 
    java.util.regex.Pattern pattern = Pattern.compile("[+-]?\\d+"); 
    return pattern.matcher(s).matches(); 
	} 
	
	public static boolean containsNumber(String s) { 
		for (int i = 0; i < s.length(); i++) {
			char ch = s.charAt(i);
			boolean isNumeric = isNumeric(ch);
			if (isNumeric) {
				return true;
			}
		}
		return false;
	} 
	
	public static boolean isAlphaNumeric(String s) { 
		boolean isNumeric = false;
		boolean isAlphabet = false;
		for (int i = 0; i < s.length(); i++) {
			char ch = s.charAt(i);
			if (!isNumeric(ch) && !isAlphabet(ch)) {
				return false;
			}
			else if (isNumeric(ch)) {
				isNumeric = true;
			}
			else if (isAlphabet(ch)) {
				isAlphabet = true;
			}
		}
		
		if (isNumeric && isAlphabet) {
			return true;
		}
		
		return false;
	} 

	public static boolean isHangulNumeric(String s) { 
		boolean isNumeric = false;
		boolean isHangul = false;
		for (int i = 0; i < s.length(); i++) {
			char ch = s.charAt(i);
			if (!isNumeric(ch) && !isHangul(ch)) {
				return false;
			}
			else if (isNumeric(ch)) {
				isNumeric = true;
			}
			else if (isHangul(ch)) {
				isHangul = true;
			}
		}
		
		if (isNumeric && isHangul) {
			return true;
		}
		
		return false;
	}

}
