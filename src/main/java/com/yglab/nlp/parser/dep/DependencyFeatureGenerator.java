package com.yglab.nlp.parser.dep;

/**
 * Interface for local context feature generators used for dependency parser.
 */
public interface DependencyFeatureGenerator<T> {

	/**
	 * Builds up the list of contextual features for the specified header and modifier position in the specified tokens
	 * sequence.
	 * 
	 * @param tokens
	 *          the tokens of the sentence
	 * @param headPosition
	 *          the position of head word
	 * @param modifierPosition
	 *          the position of modifier word
	 * @return the contextual features
	 */
	public String[] getFeatures(T tokens, int headPosition, int modifierPosition);

}
