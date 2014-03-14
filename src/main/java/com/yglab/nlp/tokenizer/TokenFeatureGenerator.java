package com.yglab.nlp.tokenizer;

/**
 * Interface for generating contextual features for maximum-entropy markov model decisions.
 * 
 * @author Younggue Bae
 */
public interface TokenFeatureGenerator {
	
	public String[] getFeatures(int position, String token);

}
