package com.yglab.nlp.ner;

import java.util.ArrayList;
import java.util.List;

import com.yglab.nlp.dictionary.RegexFeatureDictionary;



/**
 * This class generates the contextual features for named entity recognition.
 * 
 * @author Younggue Bae
 */
public class DefaultNameFeatureGenerator implements NameFeatureGenerator {
	
	protected RegexFeatureDictionary featureDic;
	
	public DefaultNameFeatureGenerator(RegexFeatureDictionary featureDic) {
		this.featureDic = featureDic;
	}
	
	@Override
	public void initialize(String[] tokens) {
		
	}
	
	/**
	 * Words is a list of the words in the entire corpus, previousLabel is the label for position-1, position-2 (or O if it's the
	 * start of a new sentence), and position is the word you are adding features for. PreviousLabel must be the only
	 * label that is visible to this method.
	 */
	@Override
	public String[] getFeatures(int position, String[] tokens, String[] previousTagSequence) {
		List<String> features = new ArrayList<String>();

		this.addUnigramFeatures(features, position, tokens, previousTagSequence);
		this.addBigramFeatures(features, position, tokens, previousTagSequence);
		this.addTrigramFeatures(features, position, tokens, previousTagSequence);
		this.addContextualFeatures(features, position, tokens, previousTagSequence);
		this.addRegexPatternFeatures(features, position, tokens, previousTagSequence);

		return features.toArray(new String[features.size()]);
	}
	
	protected void addUnigramFeatures(List<String> features, int position, String[] tokens, String[] previousTagSequence) {
		int prevLabelLength = previousTagSequence.length;
		
		String currentWord = tokens[position];
		
		// baseline features
		features.add("word=" + currentWord);
		features.add("prevLabel=" + previousTagSequence[1]);
		features.add("word=" + currentWord + ", prevLabel=" + previousTagSequence[prevLabelLength - 1]);		
	}
	
	protected void addBigramFeatures(List<String> features, int position, String[] tokens, String[] previousTagSequence) {
		String prevWord = "*";
		if (position > 0) {
			prevWord = tokens[position - 1];
		}

		// bigram feature
		features.add("prevWord=" + prevWord);
	}
	
	protected void addTrigramFeatures(List<String> features, int position, String[] tokens, String[] previousTagSequence) {
		int prevLabelLength = previousTagSequence.length;

		String prevPrevWord = "*";
		if (position > 1) {
			prevPrevWord = tokens[position - 2];
		}

		// trigram feature
		features.add("prevPrevWord=" + prevPrevWord);
		features.add("prevPrevLabel=" + previousTagSequence[prevLabelLength - 2]);
		features.add("prevLabel=" + previousTagSequence[prevLabelLength - 1] + ", prevPrevLabel=" + previousTagSequence[prevLabelLength - 2]);
	}
	
	protected void addContextualFeatures(List<String> features, int position, String[] tokens, String[] previousLabelSequence) {
		String nextWord = "STOP";
		if (position < tokens.length - 1) {
			nextWord = tokens[position + 1];
		}

		// additional features
		features.add("nextWord=" + nextWord);
	}
	
	protected void addRegexPatternFeatures(List<String> features, int position, String[] tokens, String[] previousTagSequence) {
		int prevLabelLength = previousTagSequence.length;
		String currentWord = tokens[position];
		
		String prevWord = "*";
		if (position > 0) {
			prevWord = tokens[position - 1];
		}
		
		// features from dictionary
		String[] patternFeatures = featureDic.getFeatures(currentWord);
		for (String patternFeature : patternFeatures) {
			features.add("wordPattern=" + patternFeature);
			features.add("prevLabel=" + previousTagSequence[prevLabelLength - 1] + ", wordPattern=" + patternFeature);
		}
		
		patternFeatures = featureDic.getFeatures(prevWord);
		for (String patternFeature : patternFeatures) {
			features.add("prevWordPattern=" + patternFeature);
			features.add("prevLabel=" + previousTagSequence[prevLabelLength - 1] + ", prevWordPattern=" + patternFeature);
			features.add("prevPrevLabel=" + previousTagSequence[prevLabelLength - 2] + ", prevWordPattern=" + patternFeature);
		}
	}

}
