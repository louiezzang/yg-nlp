package com.yglab.nlp.opinion;

import com.yglab.nlp.postag.POSTagger;


/**
 * This class generations tokens with its postags.
 * 
 * @author Younggue Bae
 */
public class DefaultTokenPostagPairGenerator implements TokenPostagPairGenerator {
	
	private POSTagger posTagger;
	private String delimiter;
	
	public DefaultTokenPostagPairGenerator(POSTagger posTagger) {
		this(posTagger, "\t");
	}
	
	public DefaultTokenPostagPairGenerator(POSTagger posTagger, String delimiter) {
		this.posTagger = posTagger;
		this.delimiter = delimiter;
	}

	@Override
	public String[] generate(String[] tokens) {
		String[] postags = posTagger.tag(tokens);
		for (int i = 0; i < tokens.length; i++) {
			tokens[i] = tokens[i] + delimiter + postags[i];
		}
		
		return tokens;
	}
}
