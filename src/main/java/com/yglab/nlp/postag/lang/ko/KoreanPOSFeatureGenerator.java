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
	
	private MorphemeDictionary dic;

	public KoreanPOSFeatureGenerator(MorphemeDictionary dic) {
		super();
		this.dic = dic;
	}
	
	@Override
	public String[] getFeatures(int position, String[] tokens, String[] previousLabelSequence) {
		List<String> features = new ArrayList<String>();

		this.addUnigramFeatures(features, position, tokens, previousLabelSequence);
		this.addBigramFeatures(features, position, tokens, previousLabelSequence);
		this.addTrigramFeatures(features, position, tokens, previousLabelSequence);
		this.addContextualFeatures(features, position, tokens, previousLabelSequence);
		this.addMorphoFeatures(features, position, tokens, previousLabelSequence);
		this.addMorphoFeatures(features, position, tokens, previousLabelSequence);
		
		return features.toArray(new String[features.size()]);
	}
	
	// TODO: 토큰 우측에서 좌측방향으로 탐색하면서 dic에서 매칭되는 음절(형태소)가 있을 때까지 찾도록 변경 필요.
	public List<String> findTokenSuffix(String token) {
		List<String> matchSuffixList = new ArrayList<String>();
		
		String morphDic = dic.findSuffix(token);
		
		if (morphDic == null) {
			return matchSuffixList;
		}
		
		//String tail = morphDic.split("\t")[0];
		//if (tail.equals(token)) {
		//	return matchSuffixList;
		//}
		
		String[] matchItems = morphDic.split("\\|");
		for (String matchItem : matchItems) {
			matchSuffixList.add(matchItem);
		}
		
		return matchSuffixList;
	}
	
	/**
	 * If the token contains the suffix of "josa" or "eomi", 
	 * adds the features such as the phonological type(positive or negative vowel) of jungseong 
	 * and the consonant of jongseong in last header character.
	 * 
	 */
	protected void addMorphoFeatures(List<String> features, int position, String[] tokens, String[] previousLabelSequence) {
		String currentWord = tokens[position];
		
		List<String> matchSuffixList = this.findTokenSuffix(currentWord);

		for (String matchSuffix : matchSuffixList) {
			String[] fields = matchSuffix.split("\t"); 
			
			String tail = fields[0];
			String tag = fields[1];
			
			if (tail.equals(currentWord)) {
				return;
			}
			
			// TODO: 조사 또는 어미로 끝나는 경우에만 아래 피쳐 추가
			features.add("tailTag=" + tag);
			features.add("tail=" + tail);
			
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
				
				// TODO: 어미로 끝나는 경우에만 아래 피쳐 추가
				// consonant of jongseong eomi in last header character.
				char jongseongEomi = MorphemeUtil.getJongseongEomiConsonant(lastHeaderChar);
				features.add("headerLastJongseongEomi=" + jongseongEomi);
				
				// TODO: 어미로 끝나는 경우에만 아래 피쳐 추가
				// consonant of jongseong in last header character.
				features.add("headerLastJongseong=" + MorphemeUtil.containsJongseongConsonant(lastHeaderChar));
			}
		}
	}

}
