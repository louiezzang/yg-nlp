package com.yglab.nlp.postag.lang.ko;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.yglab.nlp.postag.POSSampleParser;
import com.yglab.nlp.postag.morph.AbstractDictionary;
import com.yglab.nlp.postag.morph.Morpheme;
import com.yglab.nlp.util.lang.ko.KoreanUnicode;


/**
 * Dictionary for morphemes based on suffix(backward direction) trie structure.
 * 
 * @author Younggue Bae
 */
public class KoreanMorphemeDictionary extends AbstractDictionary<List<Morpheme>>{
	
	public KoreanMorphemeDictionary(String... files) throws IOException {
		super(files);
	}
	
	public KoreanMorphemeDictionary(int keyColumnIndex, String... files) throws IOException {
		super(keyColumnIndex, files);
	}
	
	@Override
	public void addDictionary(String item) {
		String[] field = item.split("\t");
		String dic = field[keyColumnIndex].trim();
		char[] ch = KoreanUnicode.decompose(dic);
		String key = String.valueOf(ch);
		
		Morpheme morpheme = this.toMorpheme(item);
		
		List<Morpheme> match = trieSuffix.longestMatch(key);
		if (match != null && match.size() > 0) {
			String existDic = match.get(0).getSurface();
			// if duplicate morpheme surface, add new one to the exist dictionary
			if (existDic.equals(dic)) {
				match.add(morpheme);
			}
			else {
				List<Morpheme> morphemes = new ArrayList<Morpheme>();
				morphemes.add(morpheme);
				trieSuffix.add(key, morphemes);
			}
		}
		else {
			List<Morpheme> morphemes = new ArrayList<Morpheme>();
			morphemes.add(morpheme);
			trieSuffix.add(key, morphemes);
		}
	}
	
	@Override
	public String decompose(String str) {
		char[] ch = KoreanUnicode.decompose(str);

		return String.valueOf(ch);
	}
	
	/**
	 * Converts the dictionary text to {@link Morpheme} object. 
	 * 
	 * @param str
	 * @return
	 */
	private Morpheme toMorpheme(String str) {
		Morpheme morph = new Morpheme();
		String[] fields = str.split("\t", -1);

		String surface = fields[0];
		String tag = fields[1];
		String type = fields[2];
		String[] tagItems = tag.split("\\+");

		StringBuilder sbPos = new StringBuilder();
		for (int i = 0; i < tagItems.length; i++) {
			String tagItem = tagItems[i];
			String pos = POSSampleParser.parsePos(tagItem);
			sbPos.append(pos);

			if (i < tagItems.length - 1) {
				sbPos.append("+");
			}
		}

		morph.setSurface(surface);
		morph.setTag(tag);
		morph.setPos(sbPos.toString());
		//morph.setPosDescription();

		morph.setAttribute("type", type);
		morph.setAttribute("leftMorphemeCondition", fields[3]);
		morph.setAttribute("leftMorphemeBondCondition", fields[4]);
		morph.setAttribute("leftPhonemeBondCondition", fields[5]);
		morph.setAttribute("leftPosBondCondition", fields[6]);
		morph.setAttribute("leftLemmatizationCondition", fields[7]);
		morph.setAttribute("morphemProperty", fields[8]);
		morph.setAttribute("phonemeProperty", fields[9]);

		return morph;
	}

}
