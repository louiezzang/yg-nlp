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
 * Abstract class for dictionary based on suffix(backward direction) trie structure.
 * 
 * @author Younggue Bae
 */
public abstract class AbstractDictionary<T> {
	
	protected TrieSuffixMatcher<T> trieSuffix;
	protected int keyColumnIndex = 0;
	
	/**
	 * Creates the dictionary.
	 * 
	 * @param files	The index of key column to match
	 * @throws IOException
	 */
	public AbstractDictionary(String... files) throws IOException {
		this(0, files);
	}
	
	/**
	 * Creates the dictionary.
	 * 
	 * @param keyColumnIndex	The index of key column to match
	 * @param files	The dictionary files
	 * @throws IOException
	 */
	public AbstractDictionary(int keyColumnIndex, String... files) throws IOException {
		this.keyColumnIndex = keyColumnIndex;
		this.trieSuffix = new TrieSuffixMatcher<T>();
		
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
			
			this.addDictionary(line);
		}

		in.close();
	}
	
	/**
	 * Add an item line in the dictionary file into the trie storage.
	 * 
	 * @param source The item line text in the dictionary file
	 */
	protected abstract void addDictionary(String item);
	
	/**
	 * Decomposes the source string.
	 * 
	 * @param str
	 * @return
	 */
	protected abstract String decompose(String str);
	
	/**
	 * Finds the longest suffix in the input string.
	 */
	public T findLongestSuffix(String str) {
		return trieSuffix.longestMatch(this.decompose(str));
	}

	/**
	 * Finds the shortest suffix in the input string.
	 */
	public T findShortestSuffix(String str) {
		return trieSuffix.shortestMatch(this.decompose(str));
	}
	
	/**
	 * Finds all the matched suffixes in the input string.
	 */
	public List<T> findSuffixes(String str) {
		return trieSuffix.allMatches(this.decompose(str));
	}
	
}
