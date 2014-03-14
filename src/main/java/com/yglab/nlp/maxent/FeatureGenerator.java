package com.yglab.nlp.maxent;

/**
 * Interface for context feature generators used with a viterbi algorithm.
 */
public interface FeatureGenerator<T> {

	/**
	 * Builds up the list of contextual features for the specified position in the specified tokens sequence.
	 * 
	 * @param position
	 *          the word you are adding features for
	 * @param tokens
	 *          the tokens of the sentence.
	 * @param previousLabelSequence
	 *          the sequence of labels for position-1, position-2, ... (or O if it's the start of a new sentence)
	 * @return the contextual features
	 */
	public String[] getFeatures(int position, T[] tokens, String[] previousLabelSequence);

}
