package com.yglab.nlp.util.lang.ko;

import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test case.
 * 
 * @author Younggue Bae
 */
public class KoreanMorphemeUtilTest {
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		
	}
	
	@Test
	public void testMorphemeUtil() throws Exception {
		System.out.println("truncateRight=" + String.valueOf(KoreanMorphemeUtil.truncateRight("갑니다", "ㅂ니다")));
		
		System.out.println("truncateRight=" + String.valueOf(KoreanMorphemeUtil.truncateRight("했", "었")));
		
		System.out.println("containsPositiveVowel=" + String.valueOf(KoreanMorphemeUtil.containsPositiveVowel('가')));
		
		System.out.println("getJongseongEomiConsonant=" + String.valueOf(KoreanMorphemeUtil.getJongseongEomiConsonant('갔')));
		
		System.out.println("getJongseongEomiConsonant=" + String.valueOf(KoreanMorphemeUtil.getJongseongEomiConsonant('가')));
		
		System.out.println("containsJongseongConsonant=" + String.valueOf(KoreanMorphemeUtil.containsJongseongConsonant('가')));
		
		System.out.println("appendRight=" + String.valueOf(KoreanMorphemeUtil.appendRight("갑니", "ㄹ다")));
		
		System.out.println("getJongseongConsonant=" + String.valueOf(KoreanMorphemeUtil.getJongseongConsonant('갔')));
		
		System.out.println("getJongseongConsonant=" + String.valueOf(KoreanMorphemeUtil.getJongseongConsonant('었')));
	}
}