package com.yglab.nlp.tokenizer;

import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test case.
 * 
 * @author Younggue Bae
 */
public class SimpleTokenizerTest {
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		
	}
	
	@Test
	public void testTokenizer() throws Exception {
		String text = "M.C.M(신상품) 가방이 아주 이쁘네요! 혹시, 얼마 주고 사셨나요? ";

		SimpleTokenizer tokenizer = new SimpleTokenizer();
		String[] tokens = tokenizer.tokenize(text);

		for (String token : tokens) {
			System.out.println(token);
		}
	}
}