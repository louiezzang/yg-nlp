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
		String morphSurface = field[keyColumnIndex].trim();
		//char[] ch = KoreanUnicode.decompose(morphSurface);
		//String key = String.valueOf(ch);
		String key = morphSurface;
		
		String match = trieSuffix.longestMatch(key);
		if (match != null) {
			String existMorphSurface = match.split("\t")[keyColumnIndex].trim();
			// if duplicate morpheme, concatenate new one to the exist dictionary
			if (existMorphSurface.equals(morphSurface)) {
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
