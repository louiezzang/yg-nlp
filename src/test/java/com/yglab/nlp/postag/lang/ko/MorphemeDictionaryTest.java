package com.yglab.nlp.postag.lang.ko;

import org.junit.BeforeClass;
import org.junit.Test;

import com.yglab.nlp.postag.morph.MorphemeDictionary;

/**
 * Test case.
 * 
 * @author Younggue Bae
 */
public class MorphemeDictionaryTest {
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		
	}
	
	@Test
	public void testDictionary() throws Exception {
		MorphemeDictionary dic = new MorphemeDictionary("/lang/ko/ko-pos-eomi.dic", "/lang/ko/ko-pos-josa.dic");
		
		String token = "몰라";

		System.out.println("shortest suffix = " + dic.findShortestSuffix(token));
		
		System.out.println("longest suffix = " + dic.findLongestSuffix(token));
		
		System.out.println("all suffix = " + dic.findAllSuffix(token));
		
		dic = new MorphemeDictionary(1, "/lang/ko/ko-pos-eomi.dic", "/lang/ko/ko-pos-bojo.dic");
		
		token = "위하/VV+아/EC";

		System.out.println("shortest suffix = " + dic.findShortestSuffix(token));
		
		System.out.println("longest suffix = " + dic.findLongestSuffix(token));
		
		System.out.println("all suffix = " + dic.findAllSuffix(token));
	}
}