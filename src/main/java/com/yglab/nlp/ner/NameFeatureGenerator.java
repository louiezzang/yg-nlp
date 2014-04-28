package com.yglab.nlp.ner;

import com.yglab.nlp.maxent.FeatureGenerator;


/**
 * Interface for generating contextual features for maximum-entropy markov model decisions.
 * 
 * @author Younggue Bae
 */
public interface NameFeatureGenerator extends FeatureGenerator<String> {
	
	@Override
	public void initialize(String[] tokens);
	
	@Override
	public String[] getFeatures(int position, String[] tokens, String[] previousTagSequence);
	
}
