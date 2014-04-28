package com.yglab.nlp.ner;

import java.util.ArrayList;
import java.util.List;

import com.yglab.nlp.ner.NameFeatureGenerator;
import com.yglab.nlp.util.RegexFeatureDictionary;

/**
 * This class generates the contextual features based on pattern by using postags.
 * 
 * @author Younggue Bae
 */
public class PatternBasedNameFeatureGenerator implements NameFeatureGenerator {
	
	protected RegexFeatureDictionary featureDic;
	
	public PatternBasedNameFeatureGenerator(RegexFeatureDictionary featureDic) {
		this.featureDic = featureDic;
	}
	
	@Override
	public void initialize(String[] tokens) {
		
	}
	
	@Override
	public String[] getFeatures(int position, String[] tokens, String[] previousLabelSequence) {
		List<String> features = new ArrayList<String>();

		this.addUnigramFeatures(features, position, tokens, previousLabelSequence);
		this.addBigramFeatures(features, position, tokens, previousLabelSequence);
		this.addTrigramFeatures(features, position, tokens, previousLabelSequence);
		this.addContextualFeatures(features, position, tokens, previousLabelSequence);
		this.addRegexPatternFeatures(features, position, tokens, previousLabelSequence);
		
		//System.out.println(features);

		return features.toArray(new String[features.size()]);
	}
	
	protected void addUnigramFeatures(List<String> features, int position, String[] tokens, String[] previousLabelSequence) {
		int prevLabelLength = previousLabelSequence.length;
		
		String[] currentToken = tokens[position].split("\t");
		String currentWord = currentToken[0];
		String currentPattern = currentToken[1];
		
		// baseline features
		features.add("word=" + currentWord);
		features.add("pattern=" + currentPattern);
		features.add("prevLabel=" + previousLabelSequence[1]);
		features.add("pattern=" + currentPattern + ", prevLabel=" + previousLabelSequence[prevLabelLength - 1]);		
	}
	
	protected void addBigramFeatures(List<String> features, int position, String[] tokens, String[] previousLabelSequence) {
		String prevPattern = "*";
		String prevWord = "*";
		if (position > 0) {
			String[] prevToken = tokens[position - 1].split("\t");
			prevWord = prevToken[0];
			prevPattern = prevToken[1];
		}

		// bigram feature
		features.add("prevWord=" + prevWord);
		features.add("prevPattern=" + prevPattern);
	}
	
	@SuppressWarnings("unused")
	protected void addTrigramFeatures(List<String> features, int position, String[] tokens, String[] previousLabelSequence) {
		int prevLabelLength = previousLabelSequence.length;

		String prevPrevPattern = "*";
		String prevPrevWord = "*";
		if (position > 1) {
			String[] prevPrevToken = tokens[position - 2].split("\t");
			prevPrevWord = prevPrevToken[0];
			prevPrevPattern = prevPrevToken[1];
		}

		// trigram feature
		//features.add("prevPrevWord=" + prevPrevWord);
		features.add("prevPrevPattern=" + prevPrevPattern);
		features.add("prevPrevLabel=" + previousLabelSequence[prevLabelLength - 2]);
		features.add("prevLabel=" + previousLabelSequence[prevLabelLength - 1] + ", prevPrevLabel=" + previousLabelSequence[prevLabelLength - 2]);
	}
	
	protected void addContextualFeatures(List<String> features, int position, String[] tokens, String[] previousLabelSequence) {
		String nextPattern = "STOP";
		String nextWord = "STOP";
		if (position < tokens.length - 1) {
			String[] nextToken = tokens[position + 1].split("\t");
			nextWord = nextToken[0];
			nextPattern = nextToken[1];
		}

		// additional features
		features.add("nextWord=" + nextWord);
		features.add("nextPattern=" + nextPattern);
	}
	
	@SuppressWarnings("unused")
	protected void addRegexPatternFeatures(List<String> features, int position, String[] tokens, String[] previousLabelSequence) {
		int prevLabelLength = previousLabelSequence.length;
		String[] currentToken = tokens[position].split("\t");
		String currentWord = currentToken[0];
		String currentPattern = currentToken[1];
		
		String prevPattern = "*";
		String prevWord = "*";
		if (position > 0) {
			String[] prevToken = tokens[position - 1].split("\t");
			prevWord = prevToken[0];
			prevPattern = prevToken[1];
		}
		
		// features from dictionary
		String[] patternFeatures = featureDic.getFeatures(currentWord);
		for (String patternFeature : patternFeatures) {
			if (patternFeature.equalsIgnoreCase("MODIFIER[adverb]")) {
				features.add("word=" + currentWord);
				//patternFeature = patternFeature + ":" + currentWord;
			}
			features.add("wordPattern=" + patternFeature);
			features.add("prevLabel=" + previousLabelSequence[prevLabelLength - 1] + ", wordPattern=" + patternFeature);
		}
		
		patternFeatures = featureDic.getFeatures(prevWord);
		for (String patternFeature : patternFeatures) {
			if (patternFeature.equalsIgnoreCase("MODIFIER[adverb]")) {
				//patternFeature = patternFeature + ":" + prevWord;
			}
			
			features.add("prevWordPattern=" + patternFeature);
			features.add("prevLabel=" + previousLabelSequence[prevLabelLength - 1] + ", prevWordPattern=" + patternFeature);
			features.add("prevPrevLabel=" + previousLabelSequence[prevLabelLength - 2] + ", prevWordPattern=" + patternFeature);
		}
	}

}
