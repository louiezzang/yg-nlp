package com.yglab.nlp.postag.lang.ko;

import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

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
		KoreanMorphemeDictionary dic = new KoreanMorphemeDictionary(
				"/lang/ko/ko-pos-josa.dic",
				"/lang/ko/ko-pos-eomi.dic", 
				"/lang/ko/ko-pos-bojo.dic",
				"/lang/ko/ko-pos-head.dic",
				"/lang/ko/ko-pos-word.dic",
				"/lang/ko/ko-pos-suffix.dic");

		String[] labels = KoreanPOSTagger.getLabels("/sample/ko/pos/ko-pos-train-sejong-BGAA0164.txt", "[^\\+/\\(\\)]*/", "");
		System.out.println("--------------------------");
		System.out.println("valid labels: " + labels.length);
		System.out.println("--------------------------");
		for (String label : labels) {
			System.out.println(label);
		}
		analyzer = new KoreanMorphemeAnalyzer(dic, labels);
	}

	@Test
	public void testAnalyzer() throws Exception {

		String[] tokens = {
				"이",
				"불만",
				"반드시",
				"부각되기도",
				"자아내었고",
				"올해에는",
				"전반적인",
				"등에서의",
				"등에서는",
				"부푼듯",
				"사건이었다",
				"빠른데",
				"가는데",
				"올해",
				"가셨습니다",
				"갑니다",
				"결성했지만",
				"했다",
				"일어난",
				"것이",
				"듯했으나",
				"대응함으로써",
				"울려주었다",
				"이어졌다",
				"올해는"
		};
		
		analyzer.generateCandidates(tokens); 
		List<List<Token>> candidates = analyzer.getCurrentCandidates();
		
		for (int position = 0; position < candidates.size(); position++) {
			List<Token> tokenCandidates = candidates.get(position);
			System.out.print(position + ": " + tokens[position] + " (" + tokenCandidates.size() + " candidates)");
			System.out.println(" -> tail: " + analyzer.getCurrentTailCandidates(position));
			for (Token token : tokenCandidates) {
				System.out.println("  " + token.getTag() + "\t" + token.isAnalyzed() + "\t" + token.isValidated());
			}
		}
	}
}