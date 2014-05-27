package com.yglab.nlp.postag.morph;

import java.io.IOException;


/**
 * Dictionary with plain text format based on suffix(backward direction) trie structure.
 * 
 * @author Younggue Bae
 */
public class PlainDictionary extends AbstractDictionary<String> {

	public PlainDictionary(String... files) throws IOException {
		super(files);
	}
	
	public PlainDictionary(int keyColumnIndex, String... files) throws IOException {
		super(keyColumnIndex, files);
	}
	
	@Override
	public void addDictionary(String item) {
		String[] field = item.split("\t");
		String dic = field[keyColumnIndex].trim();
		String key = dic;
		
		String match = trieSuffix.longestMatch(key);
		if (match != null) {
			String existDic = match.split("\t")[keyColumnIndex].trim();
			// if duplicate dictionary item, concatenate new one to the exist dictionary
			if (existDic.equals(dic)) {
				trieSuffix.add(key, match + "|" + item);	// "|" == "OR"
			}
			else {
				trieSuffix.add(key, item);
			}
		}
		else {
			trieSuffix.add(key, item);
		}
	}

	@Override
	public String decompose(String str) {
		// just bypass
		return str;
	}
	
}
