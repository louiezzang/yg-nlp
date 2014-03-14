package com.yglab.nlp.util.lang.ko;

import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test case.
 * 
 * @author Younggue Bae
 */
public class MorphemeUtilTest {
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		
	}
	
	@Test
	public void testMorphemeUtil() throws Exception {
		System.out.println(String.valueOf(MorphemeUtil.truncateRight("갑니다", "ㅂ니다")));
		
		System.out.println(String.valueOf(MorphemeUtil.truncateRight("했", "었")));
		
		System.out.println(String.valueOf(MorphemeUtil.containsPositiveVowel('가')));
		
		System.out.println(String.valueOf(MorphemeUtil.getJongseongEomiConsonant('갔')));
		
		System.out.println(String.valueOf(MorphemeUtil.getJongseongEomiConsonant('가')));
		
		System.out.println(String.valueOf(MorphemeUtil.containsJongseongConsonant('가')));
		
		System.out.println(String.valueOf(MorphemeUtil.appendRight("갑니", "ㄹ다")));
	}
}