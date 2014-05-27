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
				"/lang/ko/ko-pos-bojo.dic",
				"/lang/ko/ko-pos-head.dic",
				"/lang/ko/ko-pos-word.dic",
				"/lang/ko/ko-pos-suffix.dic");

		//String[] labels = KoreanPOSTagger.getLabels("/sample/ko/pos/ko-pos-train-sejong-BGAA0164.txt", "[^\\+/\\(\\)]*/", "");
		//analyzer = new KoreanMorphemeAnalyzer(dic, labels);
		analyzer = new KoreanMorphemeAnalyzer(dic);
	}

	@Test
	public void testAnalyzer() throws Exception {

		String[] tokens = {
				"이",
				"불만",
				"반드시",
				"자아내었고",
				"부각되기도",
				"올해에는",
				"전반적인",
				"등에서의",
				"부푼듯",
				"사건이었다",
				"빠른데",
				"가는데"
		};
		
		analyzer.generateCandidates(tokens); 
		List<List<Token>> candidates = analyzer.getCurrentTokensCandidates();
		
		for (int position = 0; position < candidates.size(); position++) {
			List<Token> tokenCandidates = candidates.get(position);
			System.out.println(position + ": " + tokens[position] + " (" + tokenCandidates.size() + " candidates)");
			for (Token token : tokenCandidates) {
				System.out.println("  " + token.getTag());
			}
		}
	}
}