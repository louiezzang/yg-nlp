package com.yglab.nlp.postag.lang.ko;


import java.util.ArrayList;
import java.util.List;

import com.yglab.nlp.postag.DefaultPOSFeatureGenerator;
import com.yglab.nlp.postag.morph.Token;
import com.yglab.nlp.util.StringPattern;
import com.yglab.nlp.util.StringUtil;
import com.yglab.nlp.util.lang.ko.KoreanMorphemeUtil;



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
	
	@Override
	public void initialize(String[] tokens) {
		morphAnalyzer.findTailCandidates(tokens);
	}
	
	public KoreanMorphemeAnalyzer getKoreanMorphemeAnalyzer() {
		return this.morphAnalyzer;
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
		
//		System.err.println(tokens[position]);
//		for (String feature : features) {
//			System.out.println(" " + feature);
//		}
		
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
		
		if (pattern.isAllLetter()) {
			features.add("pattern=" + "alphabet");
		}
		else if (StringUtil.containsAlphabet(currentWord)) {
			features.add("pattern=" + "containsAlphabet");
		}
	}
	
	/**
	 * If the token contains the suffix of "josa" or "eomi", 
	 * adds the features such as the phonological type(positive or negative vowel) of jungseong 
	 * and the consonant of jongseong in last header character.
	 */
	protected void addMorphoFeatures(List<String> features, int position, String[] tokens, String[] previousTagSequence) {
		List<Token> matchTailList = this.getCurrentTokenTailCandidates(position);

		StringBuilder sbBigramFeature = new StringBuilder();
		
		boolean hasPrevTail = false;
		if (position > 0) {
			List<Token> prevMatchTailList = this.getCurrentTokenTailCandidates(position - 1);

			for (int i = 0; i < prevMatchTailList.size(); i++) {
				Token matchTail = prevMatchTailList.get(i);
				features.add("prevTailTag=" + matchTail.getTag());
				if (i == 0) {
					sbBigramFeature.append(matchTail.getTag());
					hasPrevTail = true;
				}
			}
		}
		
		boolean hasCurrentTail = false;
		for (int i = 0; i < matchTailList.size(); i++) {
			Token matchTail = matchTailList.get(i);
			//if (tail.getSurface().equals(currentWord)) {
			//	return;
			//}
			
			// TODO: 조사 또는 어미로 끝나는 경우에만 아래 피쳐 추가
			features.add("tailTag=" + matchTail.getTag());
			if (i == 0 && hasPrevTail) {
				sbBigramFeature.append("," + matchTail.getTag());
				features.add("prevCurrentTailTag=" + sbBigramFeature.toString());
				hasCurrentTail = true;
			}
			else if (i == 0 && !hasPrevTail) {
				sbBigramFeature.append(matchTail.getTag());
				hasCurrentTail = true;
			}
			
			// phonological type(positive or negative vowel) of jungseong in last head character.
			String head = matchTail.getHead();
			if (!head.equals("")) {
				char lastHeadChar = head.charAt(head.length() - 1);
				boolean positiveVowel = KoreanMorphemeUtil.containsPositiveVowel(lastHeadChar);
				if (positiveVowel) {
					features.add("headLastJungseong=" + "positiveVowel");
				}
				else {
					features.add("headLastJungseong=" + "negativeVowel");
				}
				
				// consonant of jongseong in last head character.
				features.add("headLastJongseong=" + KoreanMorphemeUtil.containsJongseongConsonant(lastHeadChar));
				
				// TODO: 어미로 끝나는 경우에만 아래 피쳐 추가
				// consonant of jongseong eomi in last head character.
				char jongseongEomi = KoreanMorphemeUtil.getJongseongEomiConsonant(lastHeadChar);
				features.add("headLastJongseongEomi=" + jongseongEomi);
				
				// TODO: 어미로 끝나는 경우에만 아래 피쳐 추가
				// consonant of jongseong in last head character.
				features.add("headLastJongseong=" + KoreanMorphemeUtil.containsJongseongConsonant(lastHeadChar));
			}
		}
		
		if (position < tokens.length - 1) {
			List<Token> nextMatchTailList = this.getCurrentTokenTailCandidates(position + 1);

			for (int i = 0; i < nextMatchTailList.size(); i++) {
				Token matchTail = nextMatchTailList.get(i);
				//features.add("nextTailTag=" + matchTail.getTag());
				if (i == 0 && hasPrevTail && hasCurrentTail) {
					sbBigramFeature.append("," + matchTail.getTag());
					features.add("prevCurrentNextTailTag=" + sbBigramFeature.toString());
				}
				else if (i == 0 && hasPrevTail && !hasCurrentTail) {
					sbBigramFeature.append("," + matchTail.getTag());
					features.add("prevNextTailTag=" + sbBigramFeature.toString());
				}
				else if (i == 0 && !hasPrevTail && hasCurrentTail) {
					sbBigramFeature.append("," + matchTail.getTag());
					features.add("currentNextTailTag=" + sbBigramFeature.toString());
				}
			}
		}
	}

}
