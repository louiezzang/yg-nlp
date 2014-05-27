package com.yglab.nlp.postag.lang.ko;

import org.junit.BeforeClass;
import org.junit.Test;

import com.yglab.nlp.postag.morph.PlainDictionary;

/**
 * Test case.
 * 
 * @author Younggue Bae
 */
public class PlainDictionaryTest {
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		
	}
	
	@Test
	public void testDictionary() throws Exception {
		PlainDictionary dic = new PlainDictionary("/lang/ko/ko-pos-eomi.dic", "/lang/ko/ko-pos-josa.dic");
		
		String token = "몰라";

		System.out.println("shortest suffix = " + dic.findShortestSuffix(token));
		
		System.out.println("longest suffix = " + dic.findLongestSuffix(token));
		
		System.out.println("all suffix = " + dic.findSuffixes(token));
		
		dic = new PlainDictionary(1, "/lang/ko/ko-pos-eomi.dic", "/lang/ko/ko-pos-bojo.dic");
		
		token = "위하/VV+아/EC";

		System.out.println("shortest suffix = " + dic.findShortestSuffix(token));
		
		System.out.println("longest suffix = " + dic.findLongestSuffix(token));
		
		System.out.println("all suffix = " + dic.findSuffixes(token));
	}
}