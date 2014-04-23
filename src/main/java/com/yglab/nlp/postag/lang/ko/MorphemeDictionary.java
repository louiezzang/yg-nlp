package com.yglab.nlp.postag.lang.ko;


import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.yglab.nlp.util.lang.ko.KoreanUnicode;
import com.yglab.nlp.util.trie.TrieSuffixMatcher;



/**
 * Dictionary for Korean morphemes based on suffix(backward direction) trie structure.
 * 
 * @author Younggue Bae
 */
public class MorphemeDictionary {
	
	private TrieSuffixMatcher<String> trie;
	private int keyColumnIndex = 0;
	
	public MorphemeDictionary(String... files) throws IOException {
		this(0, files);
	}
	
	public MorphemeDictionary(int keyColumnIndex, String... files) throws IOException {
		this.keyColumnIndex = keyColumnIndex;
		this.trie = new TrieSuffixMatcher<String>();
		
		for (String file : files) {
			this.load(file);
		}
	}
	
	private void load(String filename) throws IOException {
		BufferedReader in = null;
		InputStream is = getClass().getResourceAsStream(filename);
		
		if (is != null) {
			in = new BufferedReader( new InputStreamReader(is, "utf-8"));
		}
		else {
			in = new BufferedReader(new InputStreamReader(new FileInputStream(filename), "utf-8"));
		}

		String line;
		while ((line = in.readLine()) != null) {
			if (line.startsWith("#") || line.startsWith("//") || line.trim().equals("")) {
				continue;
			}
			
			String[] field = line.split("\t");
			String morph = field[keyColumnIndex].trim();
			//String postag = field[1].trim();
			char[] ch = KoreanUnicode.decompose(morph);
			String strCh = String.valueOf(ch);
			//trie.add(strCh, postag + "_" + morph);
			
			String match = trie.longestMatch(strCh);
			if (match != null) {
				String existMorph = match.split("\t")[keyColumnIndex].trim();
				// if duplicate morpheme, concatenate new one to the exist dictionary
				if (existMorph.equals(morph)) {
					trie.add(strCh, match + "|" + line);	// "|" == "OR"
				}
				else {
					trie.add(strCh, line);
				}
			}
			else {
				trie.add(strCh, line);
			}
		}

		in.close();
	}
	
	public String findLongestMatch(String str) {
		char[] ch = KoreanUnicode.decompose(str);

		return trie.longestMatch(String.valueOf(ch));
	}
	
	public String findShortestMatch(String str) {
		char[] ch = KoreanUnicode.decompose(str);

		return trie.shortestMatch(String.valueOf(ch));
	}
	
	
	/**
	 * Finds the longest suffix in the input string.
	 */
	public String findSuffix(String str) {
		/*
		char[] ch = KoreanUnicode.decompose(str);
		
		List<String> allMatches = trie.allMatches(String.valueOf(ch));
		if (allMatches == null) {
			return null;
		}
		
		String result = null;
		String maxlenSuffix = "";
		for (String suffix : allMatches) {
			String strSuffix = suffix.split("_")[1];
			if (String.valueOf(ch).endsWith(String.valueOf(KoreanUnicode.decompose(strSuffix)))) {
				if (strSuffix.length() > maxlenSuffix.length()) {
					maxlenSuffix = strSuffix;
					result = suffix;
				}
			}
		}
		
		return result;
		*/
		
		return this.findLongestMatch(str);
	}

}
