package com.yglab.nlp.perceptron;

import java.util.List;

import com.yglab.nlp.model.Datum;



/**
 * Interface for perceptron decoder.
 * 
 * @author Younggue Bae
 */
public interface PerceptronDecoder <I, O extends Datum> {

	/**
	 * Returns true if the perceptron model was trained with labeled sample data.
	 * @return
	 */
	public boolean isLabeled();
	
	/**
	 * Returns the perceptron model.
	 * 
	 * @return
	 */
	public PerceptronModel getModel();
	
	/**
	 * Returns the golden structures of the specified sample instance.
	 * This method is only used in training time.
	 * 
	 * @param instance
	 * @return
	 */
	public List<O> getGoldenStructures(I instance);
	
	/**
	 * Returns the golden structure of the specified predicted parse.
	 * This method is only used in training time.
	 * 
	 * @param estimate
	 * @param instance
	 * @return
	 */
	public O getGoldenStructure(O estimate, I instance);

	/**
	 * Decodes the best parse.
	 * 
	 * @param instance	The sample instance
	 * @return List<O>	The best parse
	 */
	public List<O> decode(I instance);
	
	/**
	 * Decodes the K-best parses.
	 * 
	 * @param instance	The sample instance
	 * @param K	The k best
	 * @return List<List<O>>	The K-best parses
	 */
	public List<List<O>> decode(I instance, int K);
	
}
