package com.yglab.nlp.sbd;

import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test case.
 * 
 * @author Younggue Bae
 */
public class SimpleSentenceDetectorTest {
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		
	}

	@Test
	public void testSentenceDetector() throws Exception {
		String text = "나는 학교에... 갑니다. 너는 도서관에 가니? M.C.M 가방이 아주 이쁘네요! 얼마 주고 사셨나요? 좋다";
		SentenceDetector sentDetector = new SimpleSentenceDetector();
		String[] sentences = sentDetector.detect(text);

		System.out.println("\n==================================================");
		System.out.println(" detect by eos characters");
		System.out.println("--------------------------------------------------");
		for (String sentence : sentences) {
			System.out.println(sentence);
		}
	}

}