package com.yglab.nlp.postag.morph;

import java.io.IOException;


/**
 * Dictionary with plain text format based on suffix(backward direction) trie structure.
 * 
 * @author Younggue Bae
 */
public class PlainSuffixDictionary extends AbstractSuffixDictionary<String> {

	public PlainSuffixDictionary(String... files) throws IOException {
		super(files);
	}
	
	public PlainSuffixDictionary(int keyColumnIndex, String... files) throws IOException {
		super(keyColumnIndex, files);
	}
	
	@Override
	public void addDictionary(String str) {
		String[] field = str.split("\t");
		String strKey = field[keyColumnIndex].trim();
		String key = this.decompose(strKey);
		
		String match = trieSuffix.longestMatch(key);
		if (match != null) {
			String existStrKey = match.split("\t")[keyColumnIndex].trim();
			// if duplicate dictionary item, concatenate new one to the exist dictionary
			if (existStrKey.equals(strKey)) {
				trieSuffix.add(key, match + "|" + str);	// "|" == "OR"
			}
			else {
				trieSuffix.add(key, str);
			}
		}
		else {
			trieSuffix.add(key, str);
		}
	}

	@Override
	public String decompose(String str) {
		// just bypass
		return str;
	}
	
}
