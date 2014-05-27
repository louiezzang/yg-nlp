package com.yglab.nlp.util.lang.ko;

import java.util.Arrays;
import java.util.List;

/**
 * The utilities for Korean morpheme.
 * 
 * @author Younggue Bae
 */
public class KoreanMorphemeUtil {
	
	/** 중성에 들어갈수 있는 양성모음 */
	private static final Character[] POSITIVE_VOWEL = {
		'ㅏ', 'ㅐ', 'ㅑ', 'ㅒ', 'ㅗ', 'ㅘ',
		'ㅙ', 'ㅛ', 'ㅜ', 'ㅠ',
	};
	
	/** 어미중에 포함되어 용언 어간의 마지막 음절과 결할할 수 있는 자소 */
	private static final Character[] JONGSEONG_EOMI_CONSONANT = {
		'ㄴ', 'ㄹ', 'ㅂ', 'ㅆ'
	};
	
	private static final List<Character> jongseongEomis = Arrays.asList(POSITIVE_VOWEL);
	private static final List<Character> positiveVowels = Arrays.asList(JONGSEONG_EOMI_CONSONANT);


	/**
	 * Truncates the specified jaso in the right hand from the source jaso.
	 * 
	 * @param sourceJaso
	 * @param rightJaso
	 * @return
	 */
	public static char[] truncateRight(char[] sourceJaso, char[] rightJaso) {
		int truncatedLength = 0;
		int rightJasoIndex = rightJaso.length - 1;
		for (int i = sourceJaso.length - 1; i >= 0; i--) {
			if (rightJasoIndex >= 0) {
				if (sourceJaso[i] == rightJaso[rightJasoIndex]) {
					truncatedLength++;
				} else {
					break;
				}
			} else {
				break;
			}
			rightJasoIndex--;
		}

		char[] leftJaso = new char[sourceJaso.length - truncatedLength];
		for (int i = 0; i < leftJaso.length; i++) {
			leftJaso[i] = sourceJaso[i];
		}

		return leftJaso;
	}

	/**
	 * Truncates the specified string in the right hand from the source string.
	 * 
	 * @param source
	 * @param right
	 * @return
	 */
	public static String truncateRight(String source, String right) {
		
		// decompose the source string into triple jaso for each letter of the source string
		char[][] sourceTripleJaso = KoreanUnicode.decomposeTriple(source);

		// truncate the right part
		char[] leftJaso = truncateRight(KoreanUnicode.decompose(source), KoreanUnicode.decompose(right));

		// align the truncated jaso into the source triple jaso
		int stopRowIndex = -1;
		int stopColIndex = -1;
		int leftJasoIndex = 0;
		for (int i = 0; i < sourceTripleJaso.length; i++) {
			for (int j = 0; j < sourceTripleJaso[i].length; j++) {
				if (leftJasoIndex < leftJaso.length) {
					if (sourceTripleJaso[i][j] == leftJaso[leftJasoIndex]) {
						leftJasoIndex++;
					}
				} else {
					break;
				}
				stopRowIndex = i;
				stopColIndex = j;
			}
		}
		
		// if there is no left word, returns empty string
		if (stopRowIndex == -1) {
			return "";
		}

		// compose the triple characters for each letter into the letters
		char[] left = new char[stopRowIndex + 1];
		for (int i = 0; i < left.length; i++) {
			if (i < left.length - 1) {
				left[i] = KoreanUnicode.compoundJaso(sourceTripleJaso[i][0], sourceTripleJaso[i][1], sourceTripleJaso[i][2]);
			} else {
				if (stopColIndex == 2) {
					left[i] = KoreanUnicode.compoundJaso(sourceTripleJaso[i][0], sourceTripleJaso[i][1], sourceTripleJaso[i][2]);
				} else if (stopColIndex == 1) {
					left[i] = KoreanUnicode.compoundJaso(sourceTripleJaso[i][0], sourceTripleJaso[i][1], '\0');
				} else if (stopColIndex == 0) {
					left[i] = sourceTripleJaso[i][0];
				}
			}
		}

		return String.valueOf(left);
	}
	
	/**
	 * Appends the specified jaso in the right hand into the source jaso.
	 * 
	 * @param sourceJaso
	 * @param rightJaso
	 * @return
	 */
	public static char[] appendRight(char[] sourceJaso, char[] rightJaso) {
		char[] appendedJaso = new char[sourceJaso.length + rightJaso.length];
		
		int index = 0;
		for (int i = 0; i < sourceJaso.length; i++) {
			appendedJaso[index] = sourceJaso[i];
			index++;
		}
		
		for (int i = 0; i < rightJaso.length; i++) {
			appendedJaso[index] = rightJaso[i];
			index++;
		}
		
		return appendedJaso;
	}
	
	/**
	 * Appends the specified string in the right hand into the source string.
	 * 
	 * @param source
	 * @param right
	 * @return
	 */
	public static String appendRight(String source, String right) {
		if (source == null || source.length() == 0) {
			//return source;
			return right;
		}
		// decompose the last character of source string into triple jaso 
		char[] lastTripleJaso = KoreanUnicode.decomposeTriple(source.charAt(source.length() - 1));
		
		// decompose the first character of right string into triple jaso
		char[] firstRightJaso = KoreanUnicode.decomposeTriple(right.charAt(0));

		// append the right part into the last character of source string
		boolean separate = false;
		if (lastTripleJaso[0] == ' ' || lastTripleJaso[0] == '\0') {
			lastTripleJaso[0] = firstRightJaso[0];
			lastTripleJaso[1] = firstRightJaso[1];
			lastTripleJaso[2] = firstRightJaso[2];
		}
		else if (lastTripleJaso[1] == ' ' || lastTripleJaso[1] == '\0') {
			lastTripleJaso[1] = firstRightJaso[1];
			lastTripleJaso[2] = firstRightJaso[2];
		}
		else if (lastTripleJaso[2] == ' ' || lastTripleJaso[2] == '\0') {
			if ((firstRightJaso[0] != ' ' || firstRightJaso[0] != '\0') && 
					(firstRightJaso[1] == ' ' || firstRightJaso[1] == '\0') && 
					(firstRightJaso[2] == ' ' || firstRightJaso[2] == '\0')) {
				lastTripleJaso[2] = firstRightJaso[0];
			}
			else {
				separate = true;
			}
		}
		else {
			separate = true;
		}
		
		String result;
		if (separate) {
			result = source + right;
		}
		else {
			result = source.substring(0, source.length() - 1);
			result += String.valueOf(KoreanUnicode.compoundJaso(lastTripleJaso[0], lastTripleJaso[1], lastTripleJaso[2]));
			if (right.length() > 1) {
				result += right.substring(right.length() - 1);
			}
		}
		
		return result;
	}
	
	/**
	 * Returns true if one letter character contains a positive vowel in jungseong.
	 * 
	 * @param ch	The one letter character
	 * @return
	 */
	public static boolean containsPositiveVowel(char ch) {
		char[] jaso = KoreanUnicode.decomposeTriple(ch);
		char jungseong = jaso[1];
		
		if (positiveVowels.contains(jungseong)) {
			return true;
		}
		
		return false;
	}
	
	/**
	 * Returns the jongseong consonant.
	 * 
	 * @param ch	The one letter character
	 * @return
	 */
	public static char getJongseongConsonant(char ch) {
		char[] jaso = KoreanUnicode.decomposeTriple(ch);
		char jongseong = jaso[2];
		
		return jongseong;
	}
	
	/**
	 * Returns true if one letter character contains a jongseong eomi consonant('ㄴ', 'ㄹ', 'ㅂ', 'ㅆ').
	 * 
	 * @param ch	The one letter character
	 * @return
	 */
	public static boolean containsJongseongEomiConsonant(char ch) {
		char jongseong =getJongseongConsonant(ch);
		
		if (jongseongEomis.contains(jongseong)) {
			return true;
		}
		
		return false;
	}
	
	/**
	 * Returns consonant if one letter character contains a jongseong eomi consonant('ㄴ', 'ㄹ', 'ㅂ', 'ㅆ').
	 * Otherwise, it returns '\0'.
	 * 
	 * @param ch	The one letter character
	 * @return
	 */
	public static char getJongseongEomiConsonant(char ch) {
		char jongseong =getJongseongConsonant(ch);
		
		if (jongseongEomis.contains(jongseong)) {
			return jongseong;
		}
		
		return  '\0';
	}
	
	/**
	 * Returns true if one letter character contains a jongseong consonant.
	 * 
	 * @param ch	The one letter character
	 * @return
	 */
	public static boolean containsJongseongConsonant(char ch) {
		char jongseong =getJongseongConsonant(ch);
		
		if (jongseong != '\0') {
			return true;
		}
		
		return false;
	}

}
