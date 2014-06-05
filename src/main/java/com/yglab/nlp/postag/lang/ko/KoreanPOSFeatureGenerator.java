package com.yglab.nlp.postag.lang.ko;


import java.util.ArrayList;
import java.util.List;

import com.yglab.nlp.postag.DefaultPOSFeatureGenerator;
import com.yglab.nlp.postag.morph.Token;
import com.yglab.nlp.util.StringPattern;
import com.yglab.nlp.util.StringUtil;


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
	
	@Override
	public void initialize(String[] tokens) {
		morphAnalyzer.generateCandidates(tokens);
	}
	
	public List<String> getCurrentTokenTailCandidates(int position) {
		return morphAnalyzer.getCurrentTailCandidates(position);
	}
	
	public List<Token> getCurrentTokenCandidates(int position) {
		return morphAnalyzer.getCurrentCandidates(position);
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
		List<String> tailList = this.getCurrentTokenTailCandidates(position);

		StringBuilder sbBigramFeature = new StringBuilder();
		
		boolean hasPrevTail = false;
		if (position > 0) {
			List<String> prevTailList = this.getCurrentTokenTailCandidates(position - 1);

			for (int i = 0; i < prevTailList.size(); i++) {
				String prevTail = prevTailList.get(i);
				features.add("prevTailTag=" + prevTail);

				if (i == 0) {
					sbBigramFeature.append(prevTail);
					hasPrevTail = true;
					break;
				}
			}
		}
		
		boolean hasCurrentTail = false;
		for (int i = 0; i < tailList.size(); i++) {
			String tail = tailList.get(i);

			features.add("tailTag=" + tail);
			
			if (i == 0 && hasPrevTail) {
				sbBigramFeature.append("," + tail);
				features.add("prevCurrentTailTag=" + sbBigramFeature.toString());
				hasCurrentTail = true;
				break;
			}
			else if (i == 0 && !hasPrevTail) {
				sbBigramFeature.append(tail);
				hasCurrentTail = true;
				break;
			}
		}
		
		if (position < tokens.length - 1) {
			List<String> nextTailList = this.getCurrentTokenTailCandidates(position + 1);

			for (int i = 0; i < nextTailList.size(); i++) {
				String nextTail = nextTailList.get(i);

				if (i == 0 && hasPrevTail && hasCurrentTail) {
					sbBigramFeature.append("," + nextTail);
					features.add("prevCurrentNextTailTag=" + sbBigramFeature.toString());
					break;
				}
				else if (i == 0 && hasPrevTail && !hasCurrentTail) {
					sbBigramFeature.append("," + nextTail);
					features.add("prevNextTailTag=" + sbBigramFeature.toString());
					break;
				}
				else if (i == 0 && !hasPrevTail && hasCurrentTail) {
					sbBigramFeature.append("," + nextTail);
					features.add("currentNextTailTag=" + sbBigramFeature.toString());
					break;
				}
			}
		}
	}

}
