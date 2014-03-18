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
				"/lang/ko/ko-regex-feature.dic");
		String text = "!!!!";
		String[] features = featureDic.getFeatures(text);

		for (String feature : features) {
			System.out.println(feature);
		}
	}
	
}