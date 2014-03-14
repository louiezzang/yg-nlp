package com.yglab.nlp.postag;

import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test case.
 * 
 * @author Younggue Bae
 */
public class POSSampleParserTest {
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@Test
	public void testParser() throws Exception {
		String sentence = "나<NP>는<JKS> 학교<NN>에<JKB> 갑<VV-ㅂ>니다<EF+ㅂ> .<.>";
		
		POSSampleParser parser = new POSSampleParser();
		POSSample sample = parser.parse(sentence);
		
		for (int i = 0; i < sample.getSentence().length; i++) {
			System.out.println(i + ": " + sample.getSentence()[i] + "\t" + sample.getLabels()[i]);
		}
		
		System.out.println("parsePos = " + POSSampleParser.parsePos("VV-ㅂ"));
		System.out.println("parseMorpheme = " + POSSampleParser.parseMorpheme("VV_니다"));
	}
}