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
	
	public List<Token> getCurrentTokenTailCandidates(int position) {
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
		List<Token> matchTailList = this.getCurrentTokenTailCandidates(position);

		StringBuilder sbBigramFeature = new StringBuilder();
		
		boolean hasPrevTail = false;
		if (position > 0) {
			List<Token> prevMatchTailList = this.getCurrentTokenTailCandidates(position - 1);

			for (int i = 0; i < prevMatchTailList.size(); i++) {
				Token matchTail = prevMatchTailList.get(i);
				features.add("prevTailTag=" + matchTail.getTag());
				features.add("prevTail=" + matchTail.getSurface());
				// TODO: 첫번째 후보 tail만 사용을 했는데, 전체 후보 tail에 대한 조합을 추가하도록 수정 필요.
				// 조사, 어미인 경우에만 조사, 어미에 해당하는 surface tail을 추가하고 독립언(부사 등)인 경우 품사를 표시하고
				// 단독명사인 경우에는 "공백"등으로 표시함. 
				if (i == 0) {
					sbBigramFeature.append(matchTail.getTag());
					//sbBigramFeature.append(matchTail.getSurface());
					hasPrevTail = true;
				}
			}
		}
		
		boolean hasCurrentTail = false;
		for (int i = 0; i < matchTailList.size(); i++) {
			Token matchTail = matchTailList.get(i);

			features.add("tailTag=" + matchTail.getTag());
			features.add("tail=" + matchTail.getSurface());
			// TODO: 첫번째 후보 tail만 사용을 했는데, 전체 후보 tail에 대한 조합을 추가하도록 수정 필요.
			if (i == 0 && hasPrevTail) {
				sbBigramFeature.append("," + matchTail.getTag());
				//sbBigramFeature.append("," + matchTail.getSurface());
				features.add("prevCurrentTailTag=" + sbBigramFeature.toString());
				hasCurrentTail = true;
			}
			// TODO: 첫번째 후보 tail만 사용을 했는데, 전체 후보 tail에 대한 조합을 추가하도록 수정 필요.
			else if (i == 0 && !hasPrevTail) {
				sbBigramFeature.append(matchTail.getTag());
				//sbBigramFeature.append(matchTail.getSurface());
				hasCurrentTail = true;
			}

			/*
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
				
				// consonant of jongseong eomi in last head character.
				char jongseongEomi = KoreanMorphemeUtil.getJongseongEomiConsonant(lastHeadChar);
				features.add("headLastJongseongEomi=" + jongseongEomi);
				
				// consonant of jongseong in last head character.
				features.add("headLastJongseong=" + KoreanMorphemeUtil.containsJongseongConsonant(lastHeadChar));
			}
			*/
		}
		
		if (position < tokens.length - 1) {
			List<Token> nextMatchTailList = this.getCurrentTokenTailCandidates(position + 1);

			for (int i = 0; i < nextMatchTailList.size(); i++) {
				Token matchTail = nextMatchTailList.get(i);
				//features.add("nextTailTag=" + matchTail.getTag());
				// TODO: 첫번째 후보 tail만 사용을 했는데, 전체 후보 tail에 대한 조합을 추가하도록 수정 필요.
				if (i == 0 && hasPrevTail && hasCurrentTail) {
					sbBigramFeature.append("," + matchTail.getTag());
					//sbBigramFeature.append("," + matchTail.getSurface());
					features.add("prevCurrentNextTailTag=" + sbBigramFeature.toString());
				}
				// TODO: 첫번째 후보 tail만 사용을 했는데, 전체 후보 tail에 대한 조합을 추가하도록 수정 필요.
				else if (i == 0 && hasPrevTail && !hasCurrentTail) {
					sbBigramFeature.append("," + matchTail.getTag());
					//sbBigramFeature.append("," + matchTail.getSurface());
					features.add("prevNextTailTag=" + sbBigramFeature.toString());
				}
				// TODO: 첫번째 후보 tail만 사용을 했는데, 전체 후보 tail에 대한 조합을 추가하도록 수정 필요.
				else if (i == 0 && !hasPrevTail && hasCurrentTail) {
					sbBigramFeature.append("," + matchTail.getTag());
					//sbBigramFeature.append("," + matchTail.getSurface());
					features.add("currentNextTailTag=" + sbBigramFeature.toString());
				}
			}
		}
	}

}
