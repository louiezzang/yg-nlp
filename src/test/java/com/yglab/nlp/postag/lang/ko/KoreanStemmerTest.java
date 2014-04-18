package com.yglab.nlp.postag.lang.ko;

import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test case.
 * 
 * @author Younggue Bae
 */
public class KoreanStemmerTest {
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		
	}

	@Test
	public void testStemmer() throws Exception {
		KoreanStemmer stemmer = new KoreanStemmer();
		String word = "갑";
		CharSequence stem = stemmer.stem(word, "-ㅂ+ㄹ");
		System.out.println("stem == " + stem);
		
		word = "“12시지금까지";
		stem = stemmer.stem(word, "-까지");
		System.out.println("stem == " + stem);
		
		word = "내";
		stem = stemmer.stem(word, "-내");
		System.out.println("stem == " + stem);
		
		word = "새누리";
		stem = stemmer.stem(word, "-어+으");
		System.out.println("stem == " + stem);
		
		word = "고마워";
		stem = stemmer.stem(word, "-워+ㅂ");
		System.out.println("stem == " + stem);
		
		word = "했다";
		stem = stemmer.stem(word, "-다-ㅆ_하");
		System.out.println("stem == " + stem);
		
		word = "해";
		stem = stemmer.stem(word, "-해+하");
		System.out.println("stem == " + stem);
		
		word = "했";
		stem = stemmer.stem(word, "-았");
		System.out.println("stem == " + stem);
		
		word = "하셨";
		stem = stemmer.stem(word, "-시-었");
		System.out.println("stem == " + stem);
		
		word = "막혀";
		stem = stemmer.stem(word, "-여+이");
		System.out.println("stem == " + stem);
	}
}