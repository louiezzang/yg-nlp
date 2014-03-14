package com.yglab.nlp.opinion;


/**
 * The interface for generations tokens with its postags.
 * 
 * @author Younggue Bae
 */
public interface TokenPostagPairGenerator {

	public String[] generate(String[] tokens);
}
