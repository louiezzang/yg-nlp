package com.yglab.nlp.ner;


/**
 * The interface for generating the pairs of token with its postag.
 * 
 * @author Younggue Bae
 */
public interface TokenPostagPairGenerator {

	public String[] generate(String[] tokens);
}
