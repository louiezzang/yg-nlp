package com.yglab.nlp.postag.lang.ko;

import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import com.yglab.nlp.postag.morph.MorphemeDictionary;
import com.yglab.nlp.postag.morph.Token;

/**
 * Test case.
 * 
 * @author Younggue Bae
 */
public class KoreanMorphemeAnalyzerTest {
	
	private static KoreanMorphemeAnalyzer analyzer;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		MorphemeDictionary dic = new MorphemeDictionary(
				"/lang/ko/ko-pos-josa.dic",
				"/lang/ko/ko-pos-eomi.dic", 
				"/lang/ko/ko-pos-bojo.dic");

		analyzer = new KoreanMorphemeAnalyzer(dic, null);
	}

	@Test
	public void testAnalyzer() throws Exception {

		String[] tokens = {
				"자아내었고",
				"부각되기도",
				"올해에ㄴ",
				"전반적인",
		};
		
		List<List<Token>> tailCandidates = analyzer.findTailCandidates(tokens); 
		
		for (int position = 0; position < tailCandidates.size(); position++) {
			List<Token> tokenTailCandidates = tailCandidates.get(position);
			System.out.println(position + ": " + tokens[position] + " (" + tokenTailCandidates.size() + " tail candidates)");
			for (Token tail : tokenTailCandidates) {
				System.out.println("  " + tail.getTag());
			}
		}
	}
}