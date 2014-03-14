package com.yglab.nlp.util.lang.ko;

/**
 * This class converts the Korean encoding from unicode to triple encoding, and vice versa. A Korean eumjeol consists of
 * CHOSEONG(beginning consonant), JUNGSEONG(vowel), JONGSEONG(final consonant).
 * 
 * @author Younggue Bae
 */
public class KoreanUnicode {

	private static final char[] CHOSEONG = {
		'ㄱ','ㄲ','ㄴ','ㄷ','ㄸ','ㄹ','ㅁ','ㅂ','ㅃ','ㅅ',
		'ㅆ','ㅇ','ㅈ','ㅉ','ㅊ','ㅋ','ㅌ','ㅍ','ㅎ'
	};

	private static final char[] JUNGSEONG = {
		'ㅏ','ㅐ','ㅑ','ㅒ','ㅓ','ㅔ','ㅕ','ㅖ','ㅗ','ㅘ',
		'ㅙ','ㅚ','ㅛ','ㅜ','ㅝ','ㅞ','ㅟ','ㅠ','ㅡ','ㅢ',
		'ㅣ'
	};
	
	private static final char[] JONGSEONG = {
		'\0','ㄱ','ㄲ','ㄳ','ㄴ','ㄵ','ㄶ','ㄷ','ㄹ','ㄺ',
		'ㄻ','ㄼ','ㄽ','ㄾ','ㄿ','ㅀ','ㅁ','ㅂ','ㅄ','ㅅ',
		'ㅆ','ㅇ','ㅈ','ㅊ','ㅋ','ㅌ','ㅍ','ㅎ'
	};
	
	private static final int JUNG_JONG = JUNGSEONG.length * JONGSEONG.length;

	/**
	 * Converts the one Korean character to triple encoding which consists of choseung, jungseong, jongseong.
	 * 
	 * @param c
	 *          the one Korean character
	 * @return the triple encoding which consists of choseung, jungseong, jongseong
	 */
	public static char[] decompose(char c) {
		char[] result = null;

		if (c > 0xD7A3 || c < 0xAC00) {
			return new char[] { c };
		}

		c -= 0xAC00;

		char choseong = CHOSEONG[c / JUNG_JONG];
		c = (char) (c % JUNG_JONG);

		char jungseong = JUNGSEONG[c / JONGSEONG.length];
		char jongseong = JONGSEONG[c % JONGSEONG.length];

		if (jongseong != 0) {
			result = new char[] { choseong, jungseong, jongseong };
		} else {
			result = new char[] { choseong, jungseong };
		}
		return result;
	}
	
	/**
	 * Converts always the one Korean character to triple encoding which consists of choseung, jungseong, jongseong.
	 * 
	 * @param c
	 *          the one Korean character
	 * @return the triple encoding which consists of choseung, jungseong, jongseong
	 */
	public static char[] decomposeTriple(char c) {
		char[] result = null;

		if (c > 0xD7A3 || c < 0xAC00) {
			return new char[] { c, ' ', ' ' };
		}

		c -= 0xAC00;

		char choseong = CHOSEONG[c / JUNG_JONG];
		c = (char) (c % JUNG_JONG);

		char jungseong = JUNGSEONG[c / JONGSEONG.length];
		char jongseong = JONGSEONG[c % JONGSEONG.length];

		result = new char[] { choseong, jungseong, jongseong };

		return result;
	}
	
	public static char[] decompose(String s) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < s.length(); i++) {
			sb.append(decompose(s.charAt(i)));
		}
		
		return sb.toString().toCharArray();
	}
	
	/**
	 * Decomposes the source string into triple jaso for each letter of the source string.
	 * 
	 * @param s
	 * @return
	 */
	public static char[][] decomposeTriple(String s) {
		char[][] tripleJaso = new char[s.length()][3];
		for (int i = 0; i < s.length(); i++) {
			char[] chojungjong = KoreanUnicode.decomposeTriple(s.charAt(i));
			for (int j = 0; j < chojungjong.length; j++) {
				tripleJaso[i][j] = chojungjong[j];
			}
		}
		return tripleJaso;
	}
	
	public static char compound(int first, int middle, int last) {		
		return (char) (0xAC00 + first * JUNG_JONG + middle * JONGSEONG.length + last);
	}
	
	public static char compoundJaso(char first, char middle, char last) {
		// If the first char is non Korean character, it just returns its first character
		// because this case can't be made in compound jaso.
		if (indexOfChoseong(first) < 0 && middle == ' ' && last == ' ') {
			return first;
		}
		return compound(indexOfChoseong(first), indexOfJungseong(middle), indexOfJongseong(last));
	}

	public static char makeChar(char ch, int middle, int last) {		
		ch -= 0xAC00;		
		int first = ch / JUNG_JONG;		 
		return compound(first, middle, last);
	}
	
	public static char makeChar(char ch, int last) {
		ch -= 0xAC00;		
		int first = ch / JUNG_JONG;	
		ch = (char) (ch % JUNG_JONG);
		int middle = ch / JONGSEONG.length;
		
		return compound(first, middle, last);		
	}
	
	public static char replaceJongseong(char source, char replace) {
		replace -= 0xAC00;		
		int last = replace % JONGSEONG.length;
			
		return makeChar(source, last);	
	}
	
	public static int indexOfChoseong(char jaso) {
		for (int index = 0; index < CHOSEONG.length; index++) {
			char ch = CHOSEONG[index];
			if (ch == jaso) {
				return index;
			}
		}	
		return -1;
	}
	
	public static int indexOfJungseong(char jaso) {
		for (int index = 0; index < JUNGSEONG.length; index++) {
			char ch = JUNGSEONG[index];
			if (ch == jaso) {
				return index;
			}
		}	
		return -1;
	}
	
	public static int indexOfJongseong(char jaso) {
		for (int index = 0; index < JONGSEONG.length; index++) {
			char ch = JONGSEONG[index];
			if (ch == jaso) {
				return index;
			}
		}
		return -1;
	}

}
