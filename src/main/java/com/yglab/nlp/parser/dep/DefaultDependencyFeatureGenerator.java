package com.yglab.nlp.parser.dep;

import java.util.ArrayList;
import java.util.List;

import com.yglab.nlp.parser.ParseSample;
import com.yglab.nlp.util.StringPattern;



/**
 * This class generates the contextual features for dependency parser.
 * 
 * @author Younggue Bae
 */
public class DefaultDependencyFeatureGenerator implements DependencyFeatureGenerator<ParseSample> {

	public DefaultDependencyFeatureGenerator() {
	}
	
	@Override
	public String[] getFeatures(ParseSample instance, int headPosition, int modifierPosition) {
		List<String> features = new ArrayList<String>();
		
		this.addUnigramFeatures(features, instance, headPosition, modifierPosition);
		this.addBigramFeatures(features, instance, headPosition, modifierPosition);
		this.addContextualFeatures(features, instance, headPosition, modifierPosition);
		this.addInBetweenFeatures(features, instance, headPosition, modifierPosition);
		this.addDistanceFeatures(features, instance, headPosition, modifierPosition);
		
		return features.toArray(new String[features.size()]);
	}
	
	protected String normalizeWord(String word) {
		StringPattern pattern = StringPattern.recognize(word);
		
		if (pattern.isAllDigit()) {
			word = "allNum";
		}
		else if (pattern.containsDigit() && pattern.containsComma()) {
			word = "commaNum";
		}
		else if (pattern.containsDigit() && pattern.containsPeriod()) {
			word = "dotNum";
		}
		return word;
	}
	
	protected void addUnigramFeatures(List<String> features, ParseSample instance, int head, int modifier) {
		String headWord = normalizeWord(instance.forms[head]);
		String modifierWord = normalizeWord(instance.forms[modifier]);
		
		features.add("headWord=" + headWord);
		features.add("modifierWord=" + modifierWord);
		features.add("headPOS=" + instance.postags[head]);
		features.add("modifierPOS=" + instance.postags[modifier]);
		features.add("headCPOS=" + instance.cpostags[head]);
		features.add("modifierCPOS=" + instance.cpostags[modifier]);
	}
	
	protected void addBigramFeatures(List<String> features, ParseSample instance, int head, int modifier) {	
		String headWord = normalizeWord(instance.forms[head]);
		String modifierWord = normalizeWord(instance.forms[modifier]);
		
		features.add("headModifierWordPOS=" + headWord + " " + modifierWord + 
				" " + instance.postags[head] + " " + instance.postags[modifier]);
		
		features.add("headModifierWordCPOS=" + headWord + " " + modifierWord + 
				" " + instance.cpostags[head] + " " + instance.cpostags[modifier]);
		
		features.add("headModifierWord=" + headWord + " " + modifierWord);
		features.add("headModifierPOS=" + instance.postags[head] + " " + instance.postags[modifier]);
		features.add("headModifierCPOS=" + instance.cpostags[head] + " " + instance.cpostags[modifier]);
		
		// added 2014-03-29
		features.add("headCPOSModifierWordCPOS=" + instance.cpostags[head] + " " + modifierWord + 
				" " + instance.cpostags[modifier]);
		
		features.add("headWordModifierWordCPOS=" + headWord + " " + modifierWord + 
				" " + instance.cpostags[modifier]);
		
		features.add("headWordCPOSModifierCPOS=" + headWord + " " + instance.cpostags[head] + 
				" " + instance.cpostags[modifier]);
		
		features.add("headWordCPOSModifierWord=" + headWord + " " + instance.cpostags[head] + 
				" " + modifierWord);
	}
	
	protected void addContextualFeatures(List<String> features, ParseSample instance, int head, int modifier) {
		String headNextPOS = "STOP";
		String headNextCPOS = "STOP";
		String modifierNextPOS = "STOP";
		String modifierNextCPOS = "STOP";
		String modifierPrevPOS = "*";
		String modifierPrevCPOS = "*";
		String headPrevPOS = "*";
		String headPrevCPOS = "*";
		
		if (head + 1 < instance.length()) {
			headNextPOS = instance.postags[head + 1];
			headNextCPOS = instance.cpostags[head + 1];
		}
		
		if (modifier + 1 < instance.length()) {
			modifierNextPOS = instance.postags[modifier + 1];
			modifierNextCPOS = instance.cpostags[modifier + 1];
		}
		
		if (modifier - 1 > 1) {
			modifierPrevPOS = instance.postags[modifier - 1];
			modifierPrevCPOS = instance.cpostags[modifier - 1];
		}
		
		if (head - 1 > 1) {
			headPrevPOS = instance.postags[head - 1];
			headPrevCPOS = instance.cpostags[head - 1];
		}
		
		features.add("headNextmodifierPrevPOS=" + instance.postags[head] + " " + headNextPOS + 
				" " + instance.postags[modifier] + " " + modifierPrevPOS);
		
		features.add("headNextmodifierPrevCPOS=" + instance.cpostags[head] + " " + headNextCPOS + 
				" " + instance.cpostags[modifier] + " " + modifierPrevCPOS);
		
		features.add("headPrevmodifierNextPOS=" + instance.postags[head] + " " + headPrevPOS + 
				" " + instance.postags[modifier] + " " + modifierNextPOS);
		
		features.add("headPrevmodifierNextCPOS=" + instance.cpostags[head] + " " + headPrevCPOS + 
				" " + instance.cpostags[modifier] + " " + modifierNextCPOS);
	}
	

	protected void addInBetweenFeatures(List<String> features, ParseSample instance, int head, int modifier) {
		String headPOS = instance.postags[head];
		String headCPOS = instance.cpostags[head];
		String modifierPOS = instance.postags[modifier];
		String modifierCPOS = instance.cpostags[modifier];
		
		int start = -1;
		int end = -1;
		if (head > modifier) {
			start = modifier;
			end = head;
		}
		else {
			start = head;
			end = modifier;
		}
		
		for (int i = start + 1; i < end; i++) {
			String inbetweenPOS = instance.postags[i];
			String inbetweenCPOS = instance.cpostags[i];
			features.add("inbetweenPOS=" + headPOS + " " + inbetweenPOS + " " + modifierPOS);
			features.add("inbetweenCPOS=" + headCPOS + " " + inbetweenCPOS + " " + modifierCPOS);
		}
	}
	

	protected void addDistanceFeatures(List<String> features, ParseSample instance, int head, int modifier) {
		int distance = Math.abs(head - modifier);
		String direction = "left";
		if (head > modifier) {
			direction = "left";
		}
		else {
			direction = "right";
		}
		
		String distanceFeature = "";
		if (distance > 10) {
			distanceFeature = direction + 10;
		}
		else if (distance > 5) {
			distanceFeature = direction + 5;
		}
		else {
			distanceFeature = direction + (distance - 1);
		}
		
		features.add("distance=" + distanceFeature);
		
		// added 2014-03-29
		String headPOS = instance.postags[head];
		String headCPOS = instance.cpostags[head];
		String modifierPOS = instance.postags[modifier];
		String modifierCPOS = instance.cpostags[modifier];
		features.add("distance=" + headPOS + " " + modifierPOS + " " + distanceFeature);
		features.add("distance=" + headCPOS + " " + modifierCPOS + " " + distanceFeature);
	}

}
