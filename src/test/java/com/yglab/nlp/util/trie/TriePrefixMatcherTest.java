package com.yglab.nlp.util.trie;

import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test case.
 * 
 * @author Younggue Bae
 */
public class TriePrefixMatcherTest {
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		
	}
	
	@Test
	public void testMatcher() throws Exception {
		TriePrefixMatcher<String> trie = new TriePrefixMatcher<String>();
		
		trie.add("abcd", "1");
		trie.add("app", "2");
		trie.add("가", "3");
		
		String[] tests = { "a", "ab", "abc", "abcdefg", "apple", "가나" };

		for (int i = 0; i < tests.length; i++) {
			System.out.println("source: " + tests[i]);
			System.out.println("\tmatches: " + trie.matches(tests[i]));
			System.out.println("\tshortest: " + trie.shortestMatch(tests[i]));
			System.out.println("\tlongest: " + trie.longestMatch(tests[i]));
			System.out.println("\tall: " + trie.allMatches(tests[i]));
		}
	}
}