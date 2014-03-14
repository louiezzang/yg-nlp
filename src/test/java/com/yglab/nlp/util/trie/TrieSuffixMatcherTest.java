package com.yglab.nlp.util.trie;

import org.junit.BeforeClass;
import org.junit.Test;

import com.yglab.nlp.util.lang.ko.KoreanUnicode;

/**
 * Test case.
 * 
 * @author Younggue Bae
 */
public class TrieSuffixMatcherTest {
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		
	}
	
	@Test
	public void testDictionary() throws Exception {
		TrieSuffixMatcher<String> trie = new TrieSuffixMatcher<String>();
		
		trie.add("abcd", "1");
		trie.add("app", "2");
		trie.add("le", "3");
		trie.add("ple", "4");
		trie.add(String.valueOf(KoreanUnicode.decompose("가")), "5");
		trie.add(String.valueOf(KoreanUnicode.decompose("는답니다")), "6");
		trie.add(String.valueOf(KoreanUnicode.decompose("니다")), "7");
		trie.add(String.valueOf(KoreanUnicode.decompose("ㅂ니다")), "8");
		trie.add(String.valueOf(KoreanUnicode.decompose("다")), "9");
		trie.add(String.valueOf(KoreanUnicode.decompose("ㄴ다")), "10");
		trie.add(String.valueOf(KoreanUnicode.decompose("를")), "11");
		trie.add(String.valueOf(KoreanUnicode.decompose("ㄹ")), "12");
		
		String[] tests = { "xzabcd", "mobielapp", "abc", "abcdefg", "apple", "나가", "갑니다", "할" };

		for (int i = 0; i < tests.length; i++) {
			System.out.println("source: " + tests[i]);
			String chars = String.valueOf(KoreanUnicode.decompose(tests[i]));
			System.out.println("decomposed: " + chars);
			System.out.println("\tmatches: " + trie.matches(chars));
			System.out.println("\tshortest: " + trie.shortestMatch(chars));
			System.out.println("\tlongest: " + trie.longestMatch(chars));
			System.out.println("\tall: " + trie.allMatches(chars));
		}
	}
}