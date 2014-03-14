package com.yglab.nlp.perceptron;

import java.util.List;

import com.yglab.nlp.model.Datum;



/**
 * Interface for perceptron decoder.
 * 
 * @author Younggue Bae
 */
public interface PerceptronDecoder <I, O extends Datum> {

	public boolean isLabeled();
	
	public PerceptronModel getModel();
	
	public List<O> getGoldenStructures(I instance);
	
	public O getGoldenStructure(O estimate, I instance);
	
	public List<O> decode(I instance);
	
	public List<List<O>> decode(I instance, int K);
	
}
