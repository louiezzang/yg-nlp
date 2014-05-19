package com.yglab.nlp.postag.morph;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import com.yglab.nlp.util.lang.ko.KoreanUnicode;
import com.yglab.nlp.util.trie.TrieSuffixMatcher;


/**
 * Dictionary for Korean morphemes based on suffix(backward direction) trie structure.
 * 
 * @author Younggue Bae
 */
public class MorphemeDictionary {
	
	private TrieSuffixMatcher<String> trieSuffix;
	private int keyColumnIndex = 0;
	
	/**
	 * Creates the morpheme dictionary.
	 * 
	 * @param files	The index of key column to match
	 * @throws IOException
	 */
	public MorphemeDictionary(String... files) throws IOException {
		this(0, files);
	}
	
	/**
	 * Creates the morpheme dictionary.
	 * 
	 * @param keyColumnIndex	The index of key column to match
	 * @param files	The dictionary files
	 * @throws IOException
	 */
	public MorphemeDictionary(int keyColumnIndex, String... files) throws IOException {
		this.keyColumnIndex = keyColumnIndex;
		this.trieSuffix = new TrieSuffixMatcher<String>();
		
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
			char[] ch = KoreanUnicode.decompose(morph);
			String strCh = String.valueOf(ch);
			
			String match = trieSuffix.longestMatch(strCh);
			if (match != null) {
				String existMorph = match.split("\t")[keyColumnIndex].trim();
				// if duplicate morpheme, concatenate new one to the exist dictionary
				if (existMorph.equals(morph)) {
					trieSuffix.add(strCh, match + "|" + line);	// "|" == "OR"
				}
				else {
					trieSuffix.add(strCh, line);
				}
			}
			else {
				trieSuffix.add(strCh, line);
			}
		}

		in.close();
	}
	
	/**
	 * Finds the longest suffix in the input string.
	 */
	public String findLongestSuffix(String str) {
		char[] ch = KoreanUnicode.decompose(str);

		return trieSuffix.longestMatch(String.valueOf(ch));
	}

	/**
	 * Finds the shortest suffix in the input string.
	 */
	public String findShortestSuffix(String str) {
		char[] ch = KoreanUnicode.decompose(str);

		return trieSuffix.shortestMatch(String.valueOf(ch));
	}
	
	/**
	 * Finds all the matched suffix list in the input string.
	 */
	public List<String> findAllSuffix(String str) {
		char[] ch = KoreanUnicode.decompose(str);
		
		return trieSuffix.allMatches(String.valueOf(ch));
	}
	

}
