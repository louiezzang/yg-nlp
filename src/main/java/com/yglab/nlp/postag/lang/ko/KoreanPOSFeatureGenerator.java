package com.yglab.nlp.postag.lang.ko;


import java.util.ArrayList;
import java.util.List;

import com.yglab.nlp.postag.DefaultPOSFeatureGenerator;
import com.yglab.nlp.util.lang.ko.MorphemeUtil;



/**
 * This class generates the contextual features for Korean pos tagging.
 * 
 * @author Younggue Bae
 */
public class KoreanPOSFeatureGenerator extends DefaultPOSFeatureGenerator {
	
	private MorphemeDictionary dicJosa, dicEomi;
	
	public KoreanPOSFeatureGenerator(MorphemeDictionary dicJosa, MorphemeDictionary dicEomi) {
		super();
		this.dicJosa = dicJosa;
		this.dicEomi = dicEomi;
	}
	
	@Override
	public String[] getFeatures(int position, String[] tokens, String[] previousLabelSequence) {
		List<String> features = new ArrayList<String>();

		this.addUnigramFeatures(features, position, tokens, previousLabelSequence);
		this.addBigramFeatures(features, position, tokens, previousLabelSequence);
		this.addTrigramFeatures(features, position, tokens, previousLabelSequence);
		this.addContextualFeatures(features, position, tokens, previousLabelSequence);
		this.addMorphoJosaFeatures(features, position, tokens, previousLabelSequence);
		this.addMorphoEomiFeatures(features, position, tokens, previousLabelSequence);
		
		return features.toArray(new String[features.size()]);
	}
	
	/**
	 * Adds the feature which contains "josa", phonological type(positive or negative vowel) of jungseong
	 * and consonant of jongseong in last header character in last header character.
	 * 
	 * @param features
	 * @param word
	 * @return
	 */
	protected void addMorphoJosaFeatures(List<String> features, int position, String[] tokens, String[] previousLabelSequence) {
		String currentWord = tokens[position];
		
		// add "josa" morphological feature
		String tag = dicJosa.findSuffix(currentWord);
		if (tag == null) {
			return;
		}
		
		String pos = tag.split("_")[0];
		String tail = tag.split("_")[1];
		
		if (tail.equals(currentWord)) {
			return;
		}
		
		features.add("josaTag=" + tag);
		features.add("josaPos=" + pos);
		features.add("josa=" + tail);
		
		// phonological type(positive or negative vowel) of jungseong in last header character.
		String header = MorphemeUtil.truncateRight(currentWord, tail);
		if (!header.equals("")) {
			char lastHeaderChar = header.charAt(header.length() - 1);
			boolean positiveVowel = MorphemeUtil.containsPositiveVowel(lastHeaderChar);
			if (positiveVowel) {
				features.add("headerLastJungseong=" + "positiveVowel");
			}
			else {
				features.add("headerLastJungseong=" + "negativeVowel");
			}
			
			// consonant of jongseong in last header character.
			features.add("headerLastJongseong=" + MorphemeUtil.containsJongseongConsonant(lastHeaderChar));
		}
	}
	
	/**
	 * Adds the feature which contains "eomi", phonological type(positive or negative vowel) of jungseong
	 * and consonant of jongseong in last header character in last header character.
	 * 
	 * @param features
	 * @param word
	 * @return
	 */
	protected void addMorphoEomiFeatures(List<String> features, int position, String[] tokens, String[] previousLabelSequence) {
		String currentWord = tokens[position];
		
		// add "eomi" morphological feature
		String tag = dicEomi.findSuffix(currentWord);
		if (tag == null) {
			return;
		}
		
		String pos = tag.split("_")[0];
		String tail = tag.split("_")[1];
		
		if (tail.equals(currentWord)) {
			return;
		}
		
		features.add("eomiTag=" + tag);
		features.add("eomiPos=" + pos);
		features.add("eomi=" + tail);
		
		// phonological type(positive or negative vowel) of jungseong in last header character.
		String header = MorphemeUtil.truncateRight(currentWord, tail);
		if (!header.equals("")) {
			char lastHeaderChar = header.charAt(header.length() - 1);
			boolean positiveVowel = MorphemeUtil.containsPositiveVowel(lastHeaderChar);
			if (positiveVowel) {
				features.add("headerLastJungseong=" + "positiveVowel");
			}
			else {
				features.add("headerLastJungseong=" + "negativeVowel");
			}
			
			// consonant of jongseong eomi in last header character.
			char jongseongEomi = MorphemeUtil.getJongseongEomiConsonant(lastHeaderChar);
			features.add("headerLastJongseongEomi=" + jongseongEomi);
			
			// consonant of jongseong in last header character.
			features.add("headerLastJongseong=" + MorphemeUtil.containsJongseongConsonant(lastHeaderChar));
		}
	}

}
