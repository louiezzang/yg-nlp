package com.yglab.nlp.postag.lang.ko;

import org.junit.BeforeClass;
import org.junit.Test;

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
		MorphemeDictionary dic = new MorphemeDictionary("/lang/ko/ko-pos-josa.dic", "/lang/ko/ko-pos-eomi.dic");
		
		String token = "다니겠습니까";

		System.out.println("shortest match = " + dic.findShortestMatch(token));
		
		System.out.println("longest match = " + dic.findLongestMatch(token));
		
		System.out.println("suffix match = " + dic.findSuffix(token));
	}
}