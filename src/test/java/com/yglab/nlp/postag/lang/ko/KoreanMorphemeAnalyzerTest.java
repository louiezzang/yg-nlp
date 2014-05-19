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
		MorphemeDictionary baseDic = new MorphemeDictionary(
				"/lang/ko/ko-pos-josa.dic",
				"/lang/ko/ko-pos-eomi.dic", 
				"/lang/ko/ko-pos-bojo.dic");
		
		MorphemeDictionary extendedDic = new MorphemeDictionary(
				"/lang/ko/ko-pos-suffix.dic",
				"/lang/ko/ko-pos-word.dic");

		//analyzer = new KoreanMorphemeAnalyzer(baseDic, null);
		analyzer = new KoreanMorphemeAnalyzer(baseDic, extendedDic, null);
	}

	@Test
	public void testAnalyzer() throws Exception {

		String[] tokens = {
				"이",
				"자아내었고",
				"부각되기도",
				"올해에ㄴ",
				"전반적인",
				"등에서의",
				"사건이었다"
		};
		
		analyzer.findTailCandidates(tokens); 
		List<List<Token>> tailCandidates = analyzer.getCurrentTokensTailCandidates();
		
		for (int position = 0; position < tailCandidates.size(); position++) {
			List<Token> tokenTailCandidates = tailCandidates.get(position);
			System.out.println(position + ": " + tokens[position] + " (" + tokenTailCandidates.size() + " tail candidates)");
			for (Token tail : tokenTailCandidates) {
				System.out.println("  " + tail.getTag());
			}
		}
	}
}