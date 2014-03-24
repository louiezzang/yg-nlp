package com.yglab.nlp.perceptron;

import com.yglab.nlp.model.AbstractModel;
import com.yglab.nlp.model.Datum;
import com.yglab.nlp.model.EventStream;

/**
 * Perceptron trainer.
 * The averaged weight parameters perform significantly better than the simple summarized weight parameters
 * by "Discriminative Training Methods for Hidden Markov Models: Theory and Experiments with Perceptron Algorithms", 
 * Michael Collins, 2002.
 * 
 * @author Younggue Bae
 */
public class PerceptronTrainer<I, O extends Datum> {

	private PerceptronDecoder<I, O> decoder;
	
	public PerceptronTrainer(PerceptronDecoder<I, O> decoder) {
		this.decoder = decoder;
	}
	
	/**
	 * Trains a perceptron model.
	 * 
	 * @param is
	 * @param iterations
	 */
	public AbstractModel trainModel(EventStream<I, O> is, int iterations) {
		PerceptronObjectiveFunction<I, O> perceptron = new PerceptronObjectiveFunction<I, O>(is, decoder);
		MutablePerceptronModel model = (MutablePerceptronModel) decoder.getModel();
		
		for (int i = 1; i <= iterations; i++) {
			System.out.println("========================");
			System.err.println("Iteration: " + i);
			System.out.println("========================");
			long start = System.currentTimeMillis();
			if (i == 1) {
				model = perceptron.calculateInit();
				//model = perceptron.calculate();
			}
			else {
				model = perceptron.calculate();
			}
			long end = System.currentTimeMillis();
			System.out.println("Time:" + (end - start));
		}
		
		/*
		 * The averaged weight parameters perform significantly better 
		 * than the simple summarized weight parameters.
		 */
		//double[][] weights = model.to2D();
		double[][] weights = model.toAveraged2D(is.getInputStream().size(), iterations);
			
		return new PerceptronModel(model.getLabelIndex(), model.getFeatureIndex(), weights, decoder.isLabeled());
	}
	
}
