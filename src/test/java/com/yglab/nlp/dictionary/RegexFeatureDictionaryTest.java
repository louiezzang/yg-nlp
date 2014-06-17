package com.yglab.nlp.dictionary;

import org.junit.BeforeClass;
import org.junit.Test;

import com.yglab.nlp.dictionary.RegexFeatureDictionary;

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
		String text = "!!!! 20~40대까지 12월에서 내년 2월까지 20~30살까지 강모 씨(47)는 서모 씨(31·배관공)는 6만∼7만원을 1백만원은 이모씨(59·여)는 1991/01/15 10~20여일간 10~50억원짜리를 1,000억";
		String[] features = featureDic.getFeatures(text);

		for (String feature : features) {
			System.out.println(feature);
		}
		
		text = featureDic.normalizeWord(text);
		System.out.println("normalized word = " + text);
	}
	
}