package com.yglab.nlp.postag.lang.ko;


import java.util.ArrayList;
import java.util.List;

import com.yglab.nlp.postag.DefaultPOSFeatureGenerator;
import com.yglab.nlp.postag.morph.Token;
import com.yglab.nlp.util.StringPattern;
import com.yglab.nlp.util.StringUtil;
import com.yglab.nlp.util.lang.ko.MorphemeUtil;



/**
 * This class generates the contextual features for Korean pos tagging.
 * 
 * @author Younggue Bae
 */
public class KoreanPOSFeatureGenerator extends DefaultPOSFeatureGenerator {
	
	private KoreanMorphemeAnalyzer morphAnalyzer;

	public KoreanPOSFeatureGenerator(KoreanMorphemeAnalyzer morphAnalyzer) {
		super();
		this.morphAnalyzer = morphAnalyzer;
	}
	
	public List<Token> getCurrentTokenTailCandidates(int position) {
		return morphAnalyzer.getCurrentTokenTailCandidates(position);
	}
	
	public List<List<String>> getCurrentTokensTagCandidates() {
		return morphAnalyzer.getCurrentTokensTagCandidates();
	}
	
	@Override
	public void initialize(String[] tokens) {
		morphAnalyzer.findTailCandidates(tokens);
		morphAnalyzer.findTagCandidates(tokens);
	}
	
	@Override
	public String[] getFeatures(int position, String[] tokens, String[] previousLabelSequence) {
		List<String> features = new ArrayList<String>();

		this.addUnigramFeatures(features, position, tokens, previousLabelSequence);
		this.addBigramFeatures(features, position, tokens, previousLabelSequence);
		this.addTrigramFeatures(features, position, tokens, previousLabelSequence);
		this.addContextualFeatures(features, position, tokens, previousLabelSequence);
		this.addMorphoFeatures(features, position, tokens, previousLabelSequence);
		this.addWordPatternFeatures(features, position, tokens, previousLabelSequence);
		
		return features.toArray(new String[features.size()]);
	}
	
	@Override
	protected void addWordPatternFeatures(List<String> features, int position, String[] tokens, String[] previousTagSequence) {
		String currentWord = tokens[position];

		StringPattern pattern = StringPattern.recognize(currentWord);
		if (pattern.isAllDigit()) {
			features.add("pattern=" + "digit");
		}
		else if (currentWord.matches("[0-9]+[,\\.]+[0-9]+")) {
			features.add("pattern=" + "digit");
		}
		else if (pattern.containsComma()) {
			features.add("pattern=" + "containsComma");
		}
		else if (pattern.containsPeriod()) {
			features.add("pattern=" + "containsPeriod");
		}
		else if (pattern.containsSlash()) {
			features.add("pattern=" + "containsSlash");
		}
		else if (pattern.containsHyphen()) {
			features.add("pattern=" + "containsHyphen");
		}
		
		if (StringUtil.containsAlphabet(currentWord)) {
			features.add("pattern=" + "containsAlphabet");
		}
	}
	
	/**
	 * If the token contains the suffix of "josa" or "eomi", 
	 * adds the features such as the phonological type(positive or negative vowel) of jungseong 
	 * and the consonant of jongseong in last header character.
	 */
	protected void addMorphoFeatures(List<String> features, int position, String[] tokens, String[] previousTagSequence) {
		//String currentWord = tokens[position];
		List<Token> matchTailList = this.getCurrentTokenTailCandidates(position);

		for (Token matchTail : matchTailList) {
			//if (tail.getSurface().equals(currentWord)) {
			//	return;
			//}
			
			// TODO: 조사 또는 어미로 끝나는 경우에만 아래 피쳐 추가
			features.add("tailTag=" + matchTail.getTag());
			
			// phonological type(positive or negative vowel) of jungseong in last head character.
			String head = matchTail.getHead();
			if (!head.equals("")) {
				char lastHeadChar = head.charAt(head.length() - 1);
				boolean positiveVowel = MorphemeUtil.containsPositiveVowel(lastHeadChar);
				if (positiveVowel) {
					features.add("headLastJungseong=" + "positiveVowel");
				}
				else {
					features.add("headLastJungseong=" + "negativeVowel");
				}
				
				// consonant of jongseong in last head character.
				features.add("headLastJongseong=" + MorphemeUtil.containsJongseongConsonant(lastHeadChar));
				
				// TODO: 어미로 끝나는 경우에만 아래 피쳐 추가
				// consonant of jongseong eomi in last head character.
				char jongseongEomi = MorphemeUtil.getJongseongEomiConsonant(lastHeadChar);
				features.add("headLastJongseongEomi=" + jongseongEomi);
				
				// TODO: 어미로 끝나는 경우에만 아래 피쳐 추가
				// consonant of jongseong in last head character.
				features.add("headLastJongseong=" + MorphemeUtil.containsJongseongConsonant(lastHeadChar));
			}
		}
	}

}
