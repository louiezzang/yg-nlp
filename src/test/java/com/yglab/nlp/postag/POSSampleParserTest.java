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
		String sentence = "개학을<NNG+을/JKO> 앞둔<VV+ᆫ/ETM> 대학가에<NNG+에/JKB> 대규모<NNG> 신입생<NNG> 교육이<NNG+이/JKS> 한창이다<NNG+이/VCP+다/EF> .<.>";
		
		POSSampleParser parser = new POSSampleParser();
		POSSample sample = parser.parse(sentence);
		
		for (int i = 0; i < sample.getSentence().length; i++) {
			System.out.println(i + ": " + sample.getSentence()[i] + "\t" + sample.getLabels()[i]);
		}
		
		System.out.println("parsePos = " + POSSampleParser.parsePos("VV"));
		System.out.println("parsePos = " + POSSampleParser.parsePos("VV(+ㅂ)"));
		System.out.println("parsePos = " + POSSampleParser.parsePos("ㅂ니다/EF"));
		System.out.println("parsePos = " + POSSampleParser.parsePos("가다/VV(+ㅂ)"));
		System.out.println("parseMorpheme = " + POSSampleParser.parseMorpheme("EF"));
		System.out.println("parseRule = " + POSSampleParser.parseRule("가다/VV(+ㅂ)"));
	}
}