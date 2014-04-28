/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.yglab.nlp.util;

import java.lang.Character.UnicodeBlock;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {

	public static final String URL_PATTERN = "\\b(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
	public static final String HTML_TAG_PATTERN = "<(/)?([a-zA-Z]*)(\\s[a-zA-Z]*=[^>]*)?(\\s)*(/)?>";
	
	/**
	 * Determines if the specified character is a whitespace.
	 * 
	 * A character is considered a whitespace when one of the following conditions is meet:
	 * 
	 * <ul>
	 * <li>Its a {@link Character#isWhitespace(int)} whitespace.</li>
	 * <li>Its a part of the Unicode Zs category ({@link Character#SPACE_SEPARATOR}).</li>
	 * </ul>
	 * 
	 * <code>Character.isWhitespace(int)</code> does not include no-break spaces. In OpenNLP no-break
	 * spaces are also considered as white spaces.
	 * 
	 * @param charCode
	 * @return true if white space otherwise false
	 */
	public static boolean isWhitespace(char charCode) {
		return Character.isWhitespace(charCode)
				|| Character.getType(charCode) == Character.SPACE_SEPARATOR;
	}

	/**
	 * Determines if the specified character is a whitespace.
	 * 
	 * A character is considered a whitespace when one of the following conditions is meet:
	 * 
	 * <ul>
	 * <li>Its a {@link Character#isWhitespace(int)} whitespace.</li>
	 * <li>Its a part of the Unicode Zs category ({@link Character#SPACE_SEPARATOR}).</li>
	 * </ul>
	 * 
	 * <code>Character.isWhitespace(int)</code> does not include no-break spaces. In OpenNLP no-break
	 * spaces are also considered as white spaces.
	 * 
	 * @param charCode
	 * @return true if white space otherwise false
	 */
	public static boolean isWhitespace(int charCode) {
		return Character.isWhitespace(charCode)
				|| Character.getType(charCode) == Character.SPACE_SEPARATOR;
	}

	/**
	 * Converts to lower case independent of the current locale via
	 * {@link Character#toLowerCase(char)} which uses mapping information from the UnicodeData file.
	 * 
	 * @param string
	 * @return lower cased String
	 */
	public static String toLowerCase(CharSequence string) {

		char lowerCaseChars[] = new char[string.length()];

		for (int i = 0; i < string.length(); i++) {
			lowerCaseChars[i] = Character.toLowerCase(string.charAt(i));
		}

		return new String(lowerCaseChars);
	}

	/**
	 * Converts to upper case independent of the current locale via
	 * {@link Character#toUpperCase(char)} which uses mapping information from the UnicodeData file.
	 * 
	 * @param string
	 * @return upper cased String
	 */
	public static String toUpperCase(CharSequence string) {
		char upperCaseChars[] = new char[string.length()];

		for (int i = 0; i < string.length(); i++) {
			upperCaseChars[i] = Character.toUpperCase(string.charAt(i));
		}

		return new String(upperCaseChars);
	}

	/**
	 * Returns <tt>true</tt> if {@link CharSequence#length()} is <tt>0</tt> or <tt>null</tt>.
	 * 
	 * @return <tt>true</tt> if {@link CharSequence#length()} is <tt>0</tt>, otherwise <tt>false</tt>
	 * 
	 * @since 1.5.1
	 */
	public static boolean isEmpty(CharSequence theString) {
		return theString.length() == 0;
	}

	public static String base64encode(String str) {
		Base64 base = new Base64();
		byte[] strBytes = str.getBytes();
		byte[] encBytes = base.encode(strBytes);
		String encoded = new String(encBytes);
		return encoded;
	}

	public static String base64decode(String str) {
		Base64 base = new Base64();
		byte[] strBytes = str.getBytes();
		byte[] decodedBytes = base.decode(strBytes);
		String decoded = new String(decodedBytes);
		return decoded;
	}

	/**
	 * Joins the elements of the provided array into a single String containing the provided list of
	 * elements.
	 * 
	 * @param array
	 *          the array of values to join together, may be null
	 * @param separator
	 *          the separator character to use
	 * @param startIndex
	 *          the first index to start joining from. It is an error to pass in an end index past the
	 *          end of the array
	 * @param endIndex
	 *          the index to stop joining from (exclusive). It is an error to pass in an end index
	 *          past the end of the array
	 * @return the joined String, <code>null</code> if null array input
	 * 
	 * @return
	 */
	public static String join(Object[] array, String separator, int startIndex, int endIndex) {
		if (array == null) {
			return null;
		}

		int bufSize = (endIndex - startIndex);
		if (bufSize <= 0) {
			return "";
		}

		bufSize *= ((array[startIndex] == null ? 16 : array[startIndex].toString().length()) + 1);
		StringBuffer buf = new StringBuffer(bufSize);

		for (int i = startIndex; i < endIndex; i++) {
			if (i > startIndex) {
				buf.append(separator);
			}
			if (array[i] != null) {
				buf.append(array[i]);
			}
		}
		return buf.toString();
	}

	/**
	 * Joins the elements of the provided array into a single String containing the provided list of
	 * elements.
	 * 
	 * @param array
	 *          the array of values to join together, may be null
	 * @param separator
	 *          the separator character to use
	 * @return the joined String, <code>null</code> if null array input
	 * 
	 * @return
	 */
	public static String join(Object[] array, String separator) {
		if (array == null) {
			return null;
		}
		return join(array, separator, 0, array.length);
	}
	
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
		if (s == null || s.trim().length() == 0) {
			return false;
		}
		
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
		if (s == null || s.trim().length() == 0) {
			return false;
		}
		
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
		if (s == null || s.trim().length() == 0) {
			return false;
		}
		
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
		if (s == null || s.trim().length() == 0) {
			return false;
		}
		
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
	
	public static String[] extractURLs(String s) {
		List<String> urls = new ArrayList<String>();
		
		Pattern pattern = Pattern.compile(URL_PATTERN);

		Matcher matcher = pattern.matcher(s);
		while (matcher.find()) {
			urls.add(matcher.group());
		}
		
		return urls.toArray(new String[urls.size()]);
	}
}
