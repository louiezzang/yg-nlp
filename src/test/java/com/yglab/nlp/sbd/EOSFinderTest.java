package com.yglab.nlp.sbd;

import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test case.
 * 
 * @author Younggue Bae
 */
public class EOSFinderTest {
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		
	}
	
	@Test
	public void testSentenceFinder() throws Exception {
		String text = "M.C.M 가방이 아주 이쁘네요! 얼마 주고 사셨나요? ";

		char[] eosCharacters = { '.', '?', '!' };
		EOSFinder eosFinder = new EOSFinder(eosCharacters);
		List<Integer> positions = eosFinder.getPositions(text);

		for (int position : positions) {
			System.out.println(position);
		}
	}
}