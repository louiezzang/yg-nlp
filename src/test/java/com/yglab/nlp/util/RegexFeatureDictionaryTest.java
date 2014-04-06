package com.yglab.nlp.util;

import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test case.
 * 
 * @author Younggue Bae
 */
public class RegexFeatureDictionaryTest {
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		
	}
	
	@Test
	public void testDictionary() throws Exception {
		RegexFeatureDictionary featureDic = new RegexFeatureDictionary(
				//"/lang/ko/ko-regex-feature.dic",
				"/lang/ko/ko-regex-feature-unit.dic");
		String text = "!!!! 20~40대까지 12월에서 내년 2월까지 20~30살까지";
		String[] features = featureDic.getFeatures(text);

		for (String feature : features) {
			System.out.println(feature);
		}
		
		text = featureDic.normalizeWord(text);
		System.out.println("normalized word = " + text);
	}
	
}