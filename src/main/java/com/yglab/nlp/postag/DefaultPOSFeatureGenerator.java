package com.yglab.nlp.postag;

import java.util.ArrayList;
import java.util.List;

import com.yglab.nlp.util.StringPattern;

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
		this.addEnglishPatternFeatures(features, position, tokens, previousLabelSequence);
		
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
	
	protected void addEnglishPatternFeatures(List<String> features, int position, String[] tokens, String[] previousLabelSequence) {
		String currentWord = tokens[position];

		StringPattern pattern = StringPattern.recognize(currentWord);
		if (pattern.isAllDigit()) {
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
		
		if (currentWord.matches("[0-9]+[,\\.]+[0-9]+")) {
			features.add("pattern=" + "digit");
		}
		
		if (currentWord.endsWith("ly")) {
			features.add("suffix=" + "ly");
		}
		else if (currentWord.endsWith("es")) {
			features.add("suffix=" + "es");
		}
		else if (currentWord.endsWith("ed")) {
			features.add("suffix=" + "ed");
		}
		else if (currentWord.endsWith("ion")) {
			features.add("suffix=" + "ion");
		}
		else if (currentWord.endsWith("ment")) {
			features.add("suffix=" + "ment");
		}
		else if (currentWord.endsWith("ness")) {
			features.add("suffix=" + "ness");
		}
		else if (currentWord.endsWith("ate")) {
			features.add("suffix=" + "ate");
		}
		else if (currentWord.endsWith("er")) {
			features.add("suffix=" + "er");
		}
		else if (currentWord.endsWith("or")) {
			features.add("suffix=" + "or");
		}
		else if (currentWord.endsWith("est")) {
			features.add("suffix=" + "est");
		}
		else if (currentWord.endsWith("ent")) {
			features.add("suffix=" + "ent");
		}
		else if (currentWord.endsWith("ght")) {
			features.add("suffix=" + "ght");
		}
		else if (currentWord.endsWith("ive")) {
			features.add("suffix=" + "ive");
		}
		else if (currentWord.endsWith("ble")) {
			features.add("suffix=" + "ble");
		}
		else if (currentWord.endsWith("ional")) {
			features.add("suffix=" + "ional");
		}
		else if (currentWord.endsWith("s")) {
			features.add("suffix=" + "s");
		}
	}
}
