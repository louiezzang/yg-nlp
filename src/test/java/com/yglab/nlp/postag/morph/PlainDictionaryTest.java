package com.yglab.nlp.postag.morph;

import org.junit.BeforeClass;
import org.junit.Test;

import com.yglab.nlp.postag.morph.PlainSuffixDictionary;

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
		PlainSuffixDictionary dic = new PlainSuffixDictionary("/lang/ko/ko-pos-eomi.dic", "/lang/ko/ko-pos-josa.dic");
		
		String token = "몰라";

		System.out.println("token = " + token);
		
		System.out.println("shortest suffix = " + dic.findShortestSuffix(token));
		
		System.out.println("longest suffix = " + dic.findLongestSuffix(token));
		
		System.out.println("all suffixes = " + dic.findSuffixes(token));
		
		dic = new PlainSuffixDictionary(1, "/lang/ko/ko-pos-eomi.dic", "/lang/ko/ko-pos-bojo.dic");
		
		token = "위하/VV+아/EC";
		
		System.out.println("\ntoken = " + token);

		System.out.println("shortest suffix = " + dic.findShortestSuffix(token));
		
		System.out.println("longest suffix = " + dic.findLongestSuffix(token));
		
		System.out.println("all suffixes = " + dic.findSuffixes(token));
	}
}