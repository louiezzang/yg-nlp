package com.yglab.nlp.postag.lang.ko;

import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test case.
 * 
 * @author Younggue Bae
 */
public class KoreanMorphemeDictionaryTest {
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		
	}
	
	@Test
	public void testDictionary() throws Exception {
		KoreanMorphemeDictionary dic = new KoreanMorphemeDictionary(
				"/lang/ko/ko-pos-eomi.dic", 
				"/lang/ko/ko-pos-josa.dic",
				"/lang/ko/ko-pos-bojo.dic",
				"/lang/ko/ko-pos-head.dic",
				"/lang/ko/ko-pos-word.dic",
				"/lang/ko/ko-pos-suffix.dic");
		
		String token = "몰라";
		
		System.out.println("token = " + token);

		System.out.println("shortest suffix = " + dic.findShortestSuffix(token));
		
		System.out.println("longest suffix = " + dic.findLongestSuffix(token));
		
		System.out.println("all suffixes = " + dic.findSuffixes(token));
		
		/*
		dic = new KoreanMorphemeDictionary(1,
				"/lang/ko/ko-pos-eomi.dic", 
				"/lang/ko/ko-pos-josa.dic",
				"/lang/ko/ko-pos-bojo.dic",
				"/lang/ko/ko-pos-head.dic",
				"/lang/ko/ko-pos-word.dic",
				"/lang/ko/ko-pos-suffix.dic");
		
		token = "위하/VV+아/EC";
		
		System.out.println("\ntoken = " + token);

		System.out.println("shortest suffix = " + dic.findShortestSuffix(token));
		
		System.out.println("longest suffix = " + dic.findLongestSuffix(token));
		
		System.out.println("all suffixes = " + dic.findSuffixes(token));
		*/
	}
}