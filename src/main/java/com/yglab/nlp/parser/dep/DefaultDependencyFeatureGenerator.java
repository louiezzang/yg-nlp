package com.yglab.nlp.parser.dep;

import java.util.ArrayList;
import java.util.List;

import com.yglab.nlp.parser.ParseSample;



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
	
	protected void addUnigramFeatures(List<String> features, ParseSample instance, int head, int modifier) {	
		features.add("headWord=" + instance.forms[head]);
		features.add("modifierWord=" + instance.forms[modifier]);
		features.add("headPOS=" + instance.postags[head]);
		features.add("modifierPOS=" + instance.postags[modifier]);
		features.add("headCPOS=" + instance.cpostags[head]);
		features.add("modifierCPOS=" + instance.cpostags[modifier]);
	}
	
	protected void addBigramFeatures(List<String> features, ParseSample instance, int head, int modifier) {	
		features.add("headModifierWordPOS=" + instance.forms[head] + " " + instance.forms[modifier] + 
				" " + instance.postags[head] + " " + instance.postags[modifier]);
		
		features.add("headModifierWordCPOS=" + instance.forms[head] + " " + instance.forms[modifier] + 
				" " + instance.cpostags[head] + " " + instance.cpostags[modifier]);
		
		features.add("headModifierWord=" + instance.forms[head] + " " + instance.forms[modifier]);
		features.add("headModifierPOS=" + instance.postags[head] + " " + instance.postags[modifier]);
		features.add("headModifierCPOS=" + instance.cpostags[head] + " " + instance.cpostags[modifier]);
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
		
		for (int i = start; i < end; i++) {
			String inbetweenPOS = instance.postags[i];
			String inbetweenCPOS = instance.cpostags[i];
			features.add("inbetweenPOS=" + headPOS + " " + inbetweenPOS + " " + modifierPOS);
			features.add("inbetweenCPOS=" + headCPOS + " " + inbetweenCPOS + " " + modifierCPOS);
		}
	}
	

	protected void addDistanceFeatures(List<String> features, ParseSample instance, int head, int modifier) {
		//String headPOS = instance.postags[head];
		//String headCPOS = instance.cpostags[head];
		//String modifierPOS = instance.postags[modifier];
		//String modifierCPOS = instance.cpostags[modifier];
		
		int distance = Math.abs(head - modifier);
		String direction = "lefthand";
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
		
		//features.add("distance=" + headPOS + " " + modifierPOS + " " + distanceFeature);
		//features.add("distance=" + headCPOS + " " + modifierCPOS + " " + distanceFeature);
		
		features.add("distance=" + distanceFeature);
	}

}