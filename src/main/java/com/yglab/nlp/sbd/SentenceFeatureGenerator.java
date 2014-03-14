package com.yglab.nlp.sbd;

import com.yglab.nlp.maxent.FeatureGenerator;


/**
 * Interface for generating contextual features for maximum-entropy markov model decisions.
 * 
 * @author Younggue Bae
 */
public interface SentenceFeatureGenerator extends FeatureGenerator<String> {
	
	@Override
	public String[] getFeatures(int position, String[] tokens, String[] previousLabelSequence);
}
