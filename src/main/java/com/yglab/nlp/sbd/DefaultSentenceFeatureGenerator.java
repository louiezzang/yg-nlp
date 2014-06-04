package com.yglab.nlp.sbd;

import java.util.ArrayList;
import java.util.List;

import com.yglab.nlp.dictionary.RegexFeatureDictionary;

/**
 * This class generates the contextual features for sentence detector.
 * 
 * @author Younggue Bae
 */
public class DefaultSentenceFeatureGenerator implements SentenceFeatureGenerator {
	
	private RegexFeatureDictionary featureDic;
	
	public DefaultSentenceFeatureGenerator(RegexFeatureDictionary featureDic) {
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

		this.addPrefixFeatures(features, position, tokens, previousTagSequence);
		this.addSuffixFeatures(features, position, tokens, previousTagSequence);
		this.addRegexPatternFeatures(features, position, tokens, previousTagSequence);
		this.addSuffixWithPunctuationFeatures(features, position, tokens, previousTagSequence);
		
		// TODO: features to add
		// endsWithEomi?

		return features.toArray(new String[features.size()]);
	}
	
	@SuppressWarnings("unused")
	protected void addPrefixFeatures(List<String> features, int position, String[] tokens, String[] previousTagSequence) {
		//int prevLabelLength = previousTagSequence.length;
		
		String currentWord = tokens[position];
		
		String prevWord = "<START>";
		if (position > 0) {
			prevWord = tokens[position - 1];
		}
		
		String nextWord = "<STOP>";
		if (position < tokens.length - 1) {
			nextWord = tokens[position + 1];
		}
		
		//features.add("prefix=" + getPrefix(currentWord, 2));
		//features.add("prevPrefix=" + getPrefix(prevWord, 2));
		//features.add("nextPrefix=" + getPrefix(nextWord, 2));
		
		features.add("prevPrefix=" + getPrefix(prevWord, 2) + ", nextPrefix=" + getPrefix(nextWord, 2));
	}
	
	protected void addSuffixFeatures(List<String> features, int position, String[] tokens, String[] previousTagSequence) {
		//int prevLabelLength = previousTagSequence.length;
		
		String currentWord = tokens[position];
		
		String prevWord = "<START>";
		if (position > 0) {
			prevWord = tokens[position - 1];
		}
		
		String nextWord = "<STOP>";
		if (position < tokens.length - 1) {
			nextWord = tokens[position + 1];
		}
		
		features.add("suffix=" + getSuffix(currentWord, 2));
		//features.add("prevSuffix=" + getSuffix(prevWord, 2));
		//features.add("nextSuffix=" + getSuffix(nextWord, 2));
		
		features.add("prevSuffix=" + getSuffix(prevWord, 2) + ", nextSuffix=" + getSuffix(nextWord, 2));
	}
	
	private String getPrefix(String s, int length) {
		String prefix = null;
		if (s.equals("<START>") || s.equals("<STOP>")) {
			prefix = s;
		}
		else if (s.length() >= length) {
			prefix = s.substring(0, length);
		}
		else if (s.length() == 1) {
			prefix = s.substring(0, 1);
		}
		
		return prefix;
	}
	
	private String getSuffix(String s, int length) {
		String suffix = null;
		if (s.equals("<START>") || s.equals("<STOP>")) {
			suffix = s;
		}
		else if (s.length() >= length) {
			suffix = s.substring(s.length() - length);
		}
		else if (s.length() == 1) {
			suffix = s.substring(s.length() - 1);
		}
		
		return suffix;
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
	
	protected void addSuffixWithPunctuationFeatures(List<String> features, int position, String[] tokens, String[] previousTagSequence) {
		String currentWord = tokens[position];
		
		if (currentWord.matches("([^\\.]+\\.{2,}$)|([^\\?]+\\?{2,}$)|([^!]+!{2,}$)|(.*[\\?!][\\?!]$)")) {
			String pureWord = currentWord.replaceAll("\\.+|\\?+|!+", "");
			if (pureWord.length() > 0) {
				features.add("suffix=" + this.getSuffix(pureWord, 2));
			}
		}
	}

}
