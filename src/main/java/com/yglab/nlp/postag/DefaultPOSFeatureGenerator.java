package com.yglab.nlp.postag;

import java.util.ArrayList;
import java.util.List;

/**
 * This class generates the contextual features for pos tagging.
 * 
 * @author Younggue Bae
 */
public class DefaultPOSFeatureGenerator implements POSFeatureGenerator {
	
	public DefaultPOSFeatureGenerator() {
	}
	
	@Override
	public String[] getFeatures(int position, String[] tokens, String[] previousLabelSequence) {
		List<String> features = new ArrayList<String>();

		this.addUnigramFeatures(features, position, tokens, previousLabelSequence);
		this.addBigramFeatures(features, position, tokens, previousLabelSequence);
		this.addTrigramFeatures(features, position, tokens, previousLabelSequence);
		this.addContextualFeatures(features, position, tokens, previousLabelSequence);
		
		return features.toArray(new String[features.size()]);
	}
	
	protected void addUnigramFeatures(List<String> features, int position, String[] tokens, String[] previousLabelSequence) {
		int prevLabelLength = previousLabelSequence.length;

		String currentWord = tokens[position];

		features.add("word=" + currentWord);
		features.add("prevLabel=" + previousLabelSequence[1]);
		features.add("word=" + currentWord + ", prevLabel=" + previousLabelSequence[prevLabelLength - 1]);
	}
	
	protected void addBigramFeatures(List<String> features, int position, String[] tokens, String[] previousLabelSequence) {
		String prevWord = "*";
		if (position > 0) {
			prevWord = tokens[position - 1];
		}

		// bigram feature
		features.add("prevWord=" + prevWord);
	}
	
	protected void addTrigramFeatures(List<String> features, int position, String[] tokens, String[] previousLabelSequence) {
		int prevLabelLength = previousLabelSequence.length;

		String prevPrevWord = "*";
		if (position > 1) {
			prevPrevWord = tokens[position - 2];
		}
		
		// trigram feature
		features.add("prevPrevWord=" + prevPrevWord);
		features.add("prevPrevLabel=" + previousLabelSequence[prevLabelLength - 2]);
		features.add("prevLabel=" + previousLabelSequence[prevLabelLength - 1] + ", prevPrevLabel=" + previousLabelSequence[prevLabelLength - 2]);
	}
	
	protected void addContextualFeatures(List<String> features, int position, String[] tokens, String[] previousLabelSequence) {
		String nextWord = "STOP";
		if (position < tokens.length - 1) {
			nextWord = tokens[position + 1];
		}

		// additional features
		features.add("nextWord=" + nextWord);
	}
}
