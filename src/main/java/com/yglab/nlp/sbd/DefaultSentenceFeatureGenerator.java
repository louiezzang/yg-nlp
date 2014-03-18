package com.yglab.nlp.sbd;

import java.util.ArrayList;
import java.util.List;

import com.yglab.nlp.util.RegexFeatureDictionary;

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
	
	/**
	 * Words is a list of the words in the entire corpus, previousLabel is the label for position-1, position-2 (or O if it's the
	 * start of a new sentence), and position is the word you are adding features for. PreviousLabel must be the only
	 * label that is visible to this method.
	 */
	@Override
	public String[] getFeatures(int position, String[] tokens, String[] previousLabelSequence) {
		List<String> features = new ArrayList<String>();

		this.addPrefixFeatures(features, position, tokens, previousLabelSequence);
		this.addSuffixFeatures(features, position, tokens, previousLabelSequence);
		
		// TODO features to add
		// addRegexMatchPatterns
		// endsWithEomi?
		// endsWithPunctuation
		// endsWithEmoticon
		// endsWithBrace
		// startsWithBrace

		return features.toArray(new String[features.size()]);
	}
	
	@SuppressWarnings("unused")
	protected void addPrefixFeatures(List<String> features, int position, String[] tokens, String[] previousLabelSequence) {
		//int prevLabelLength = previousLabelSequence.length;
		
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
	
	protected void addSuffixFeatures(List<String> features, int position, String[] tokens, String[] previousLabelSequence) {
		//int prevLabelLength = previousLabelSequence.length;
		
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

	protected void addDictionaryPatternFeatures(List<String> features, int position, String[] tokens, String[] previousLabelSequence) {
		int prevLabelLength = previousLabelSequence.length;
		String currentWord = tokens[position];
		
		String prevWord = "*";
		if (position > 0) {
			prevWord = tokens[position - 1];
		}
		
		// features from dictionary
		String[] dicPatterns = featureDic.getFeatures(currentWord);
		for (String dicPattern : dicPatterns) {
			features.add("wordPattern=" + dicPattern);
			features.add("prevLabel=" + previousLabelSequence[prevLabelLength - 1] + ", wordPattern=" + dicPattern);
		}
		
		dicPatterns = featureDic.getFeatures(prevWord);
		for (String dicPattern : dicPatterns) {
			features.add("prevWordPattern=" + dicPattern);
			features.add("prevLabel=" + previousLabelSequence[prevLabelLength - 1] + ", prevWordPattern=" + dicPattern);
			features.add("prevPrevLabel=" + previousLabelSequence[prevLabelLength - 2] + ", prevWordPattern=" + dicPattern);
		}
	}

}