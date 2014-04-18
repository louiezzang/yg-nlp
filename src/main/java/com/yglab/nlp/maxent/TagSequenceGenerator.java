package com.yglab.nlp.maxent;

import java.util.List;

import com.yglab.nlp.model.Datum;

/**
 * Interface for generating the possible tag sequence candidates(GEN).
 * GEN is a function that maps an input x to a set of candidates GEN(x). 
 * e.g. Tagging: GEN(x) is the set of all possible tag sequences with the specified length.
 * 
 * @author Younggue Bae
 */
public interface TagSequenceGenerator {

	/**
	 * Gets all the possible previous label sequence candidates of each token.
	 * 
	 * @param tokens	the token of sentence for test
	 * @return
	 */
	public List<List<Datum>> getCandidates(String[] tokens);
}
